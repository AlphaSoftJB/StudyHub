/**
 * Axios client — JWT stored in-memory only (never localStorage).
 * DR-016: Access tokens in-memory; refresh tokens in httpOnly cookie.
 */
import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: BASE_URL,
  withCredentials: true, // sends httpOnly refresh token cookie
  headers: { 'Content-Type': 'application/json' },
});

// In-memory JWT store — intentionally NOT localStorage (security constraint)
let _accessToken: string | null = null;

export function setAccessToken(token: string | null) {
  _accessToken = token;
}

export function clearAccessToken() {
  _accessToken = null;
}

// Attach JWT to every request when present
apiClient.interceptors.request.use((config) => {
  if (_accessToken) {
    config.headers.Authorization = `Bearer ${_accessToken}`;
  }
  return config;
});

// 401 → clear token (actual refresh logic belongs in AuthContext, Sprint 4)
apiClient.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      clearAccessToken();
    }
    return Promise.reject(err);
  },
);
