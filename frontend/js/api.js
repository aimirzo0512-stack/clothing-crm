/*
 * Tiny fetch wrapper. Attaches the JWT, parses the standard ApiResponse
 * envelope, shows a global loader, and surfaces errors as toasts.
 */
const API = (() => {
  const base = () => window.APP_CONFIG.API_BASE;

  function token() { return localStorage.getItem('token'); }

  function showLoader(on) {
    const el = document.getElementById('loading-overlay');
    if (el) el.classList.toggle('open', on);
  }

  async function request(method, path, body, opts = {}) {
    showLoader(true);
    try {
      const headers = { 'Content-Type': 'application/json' };
      const t = token();
      if (t) headers['Authorization'] = 'Bearer ' + t;

      const res = await fetch(base() + path, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined
      });

      if (res.status === 401) {
        localStorage.clear();
        if (!location.pathname.endsWith('index.html')) location.href = 'index.html';
        throw new Error('Session expired. Please log in again.');
      }
      if (opts.raw) return res;

      const json = await res.json().catch(() => ({}));
      if (!res.ok || json.success === false) {
        throw new Error(json.message || ('Request failed (' + res.status + ')'));
      }
      return json.data;
    } finally {
      showLoader(false);
    }
  }

  return {
    get: (p) => request('GET', p),
    post: (p, b) => request('POST', p, b),
    put: (p, b) => request('PUT', p, b),
    patch: (p, b) => request('PATCH', p, b),
    del: (p) => request('DELETE', p),
    raw: (p) => request('GET', p, null, { raw: true })
  };
})();
