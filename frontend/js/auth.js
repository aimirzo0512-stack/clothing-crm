/* Authentication helpers shared by every page. */
const Auth = {
  save(data) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({
      userId: data.userId, username: data.username,
      fullName: data.fullName, role: data.role
    }));
  },
  user() {
    try { return JSON.parse(localStorage.getItem('user')); } catch { return null; }
  },
  role() { return (this.user() || {}).role; },
  isLoggedIn() { return !!localStorage.getItem('token'); },
  logout() { localStorage.clear(); location.href = 'index.html'; },
  // Redirect to login if not authenticated. Call at the top of protected pages.
  guard() {
    if (!this.isLoggedIn()) { location.href = 'index.html'; }
  },
  // True if the current user has any of the supplied roles.
  can(...roles) { return roles.includes(this.role()); }
};
