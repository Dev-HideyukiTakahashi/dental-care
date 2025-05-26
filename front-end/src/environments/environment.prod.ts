declare global {
  interface Window {
    __env: any;
  }
}

const env = window.__env || {};

export const environment = {
  production: true,
  api: env.api || '',
  authApi: env.authApi || '',
  clientId: env.clientId || '',
  clientSecret: env.clientSecret || '',
};
