// // Global configuration. Change API_BASE to point at your backend.
// window.APP_CONFIG = {
//   API_BASE: window.localStorage.getItem('apiBase') || 'http://localhost:8080/api'
// };

window.APP_CONFIG = {
  API_BASE: window.localStorage.getItem('apiBase') ||
      `${window.location.protocol}//${window.location.hostname}/api`
};