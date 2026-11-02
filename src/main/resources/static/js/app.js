// =======================================================
// BloodConnect API Client & Utility Library
// All backend API calls go through this module
// =======================================================

const API = {
  BASE: '/api',

  // --- Auth Endpoints ---
  async register(data)  { return this._post('/auth/register', data); },
  async login(data)     { return this._post('/auth/login', data); },
  async logout()        { return this._post('/auth/logout', {}); },
  async currentUser()   { return this._get('/auth/me'); },

  // --- Donor Endpoints ---
  async searchDonors(params = {}) {
    const qs = new URLSearchParams(Object.fromEntries(Object.entries(params).filter(([,v])=>v!==''&&v!==null&&v!==undefined))).toString();
    return this._get(`/donors/search${qs ? '?' + qs : ''}`);
  },
  async getDonor(id)              { return this._get(`/donors/${id}`); },
  async getMyDonorProfile()       { return this._get('/donors/profile/me'); },
  async updateAvailability(flag)  { return this._put('/donors/availability', { isAvailable: flag }); },
  async getDonationHistory()      { return this._get('/donors/history/me'); },
  async recordDonation(data)      { return this._post('/donors/history', data); },

  // --- Blood Request Endpoints ---
  async createRequest(data)              { return this._post('/requests', data); },
  async searchRequests(params = {})      {
    const qs = new URLSearchParams(Object.fromEntries(Object.entries(params).filter(([,v])=>v!==''&&v!==null&&v!==undefined))).toString();
    return this._get(`/requests${qs ? '?' + qs : ''}`);
  },
  async getEmergencyRequests()           { return this._get('/requests/emergency'); },
  async getMyRequests()                  { return this._get('/requests/my'); },
  async getRequest(id)                   { return this._get(`/requests/${id}`); },
  async updateRequestStatus(id, data)    { return this._put(`/requests/${id}/status`, data); },
  async cancelRequest(id)                { return this._put(`/requests/${id}/cancel`, {}); },

  // --- Admin Endpoints ---
  async getAdminStats()     { return this._get('/admin/stats'); },
  async getAllUsers()        { return this._get('/admin/users'); },
  async toggleUserStatus(id){ return this._put(`/admin/users/${id}/toggle-status`, {}); },
  async getAllDonors()       { return this._get('/admin/donors'); },
  async getAllRequests()     { return this._get('/admin/requests'); },

  // --- Dashboard/Public ---
  async getDashboardStats() { return this._get('/dashboard/stats'); },
  async getHealth()         { return this._get('/health'); },

  // --- User Profile Endpoints ---
  async getMyProfile()      { return this._get('/users/me/profile'); },
  async updateProfile(data) { return this._put('/users/me/profile', data); },

  // --- Core HTTP Methods ---
  async _get(path) {
    const res = await fetch(this.BASE + path, { credentials: 'include' });
    return this._handle(res);
  },
  async _post(path, body) {
    const res = await fetch(this.BASE + path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(body)
    });
    return this._handle(res);
  },
  async _put(path, body) {
    const res = await fetch(this.BASE + path, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify(body)
    });
    return this._handle(res);
  },
  async _handle(res) {
    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      // Extract a user-friendly string message from various response shapes
      let msg = 'Request failed';
      if (typeof data.message === 'string' && data.message) {
        msg = data.message;
      } else if (data.error && typeof data.error === 'string') {
        msg = data.error;
      } else if (data.errors) {
        // Validation errors map: pick first one
        const vals = Object.values(data.errors);
        if (vals.length) msg = vals[0];
      }
      throw { status: res.status, message: msg, data };
    }
    return data;
  }
};

// =======================================================
// Session & Auth Helpers
// =======================================================
const Session = {
  KEY_USER: 'bc_user',

  save(user) { localStorage.setItem(this.KEY_USER, JSON.stringify(user)); },
  get()      {
    try { return JSON.parse(localStorage.getItem(this.KEY_USER)); }
    catch { return null; }
  },
  clear()    { localStorage.removeItem(this.KEY_USER); },

  isLoggedIn() { return !!this.get(); },

  getRole() {
    const u = this.get();
    return u ? u.role : null;
  },

  requireAuth(redirectTo = '/login.html') {
    if (!this.isLoggedIn()) {
      window.location.href = redirectTo;
      return null;
    }
    return this.get();
  },

  requireRole(role, redirectTo = '/index.html') {
    const user = this.requireAuth();
    if (!user) return null;
    if (user.role !== role) {
      window.location.href = redirectTo;
      return null;
    }
    return user;
  },

  requireAdmin()     { return this.requireRole('ADMIN', '/index.html'); },
  requireDonor()     { return this.requireRole('DONOR', '/index.html'); },
  requireRequester() { return this.requireRole('REQUESTER', '/index.html'); },

  async syncWithServer() {
    try {
      const res = await API.currentUser();
      if (res.success && res.data) {
        this.save(res.data);
        return res.data;
      }
    } catch (e) {
      this.clear();
    }
    return null;
  }
};

// =======================================================
// Toast Notification System
// =======================================================
const Toast = {
  container: null,
  icons: { success:'✅', error:'❌', warning:'⚠️', info:'ℹ️' },

  init() {
    if (!this.container) {
      this.container = document.createElement('div');
      this.container.className = 'toast-container';
      this.container.id = 'toast-container';
      document.body.appendChild(this.container);
    }
  },

  show(message, type = 'info', duration = 4000) {
    this.init();
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
      <span class="toast-icon">${this.icons[type] || this.icons.info}</span>
      <span class="toast-message">${message}</span>
      <span class="toast-close" onclick="this.closest('.toast').remove()">✕</span>
    `;
    this.container.appendChild(toast);
    setTimeout(() => {
      toast.classList.add('hiding');
      setTimeout(() => toast.remove(), 350);
    }, duration);
    return toast;
  },

  success(msg, dur) { return this.show(msg, 'success', dur); },
  error(msg, dur)   { return this.show(msg, 'error', dur || 6000); },
  warning(msg, dur) { return this.show(msg, 'warning', dur); },
  info(msg, dur)    { return this.show(msg, 'info', dur); }
};

// =======================================================
// Navbar
// =======================================================
const Navbar = {
  init() {
    const navbar = document.getElementById('navbar');
    if (!navbar) return;

    // Scroll effect
    window.addEventListener('scroll', () => {
      navbar.classList.toggle('scrolled', window.scrollY > 20);
    }, { passive: true });

    // Hamburger
    const hamburger = document.getElementById('hamburger');
    const navLinks  = document.getElementById('nav-links');
    const navActions = document.getElementById('nav-actions');
    if (hamburger) {
      hamburger.addEventListener('click', () => {
        navLinks?.classList.toggle('open');
        navActions?.classList.toggle('open');
      });
    }

    // User session
    this.updateUserSection();
  },

  updateUserSection() {
    const user = Session.get();
    const guestEl   = document.getElementById('nav-guest');
    const userEl    = document.getElementById('nav-user');
    const userNameEl = document.getElementById('nav-user-name');
    const avatarEl   = document.getElementById('nav-avatar');
    const dashLink   = document.getElementById('nav-dashboard-link');

    if (user) {
      if (guestEl) guestEl.style.display = 'none';
      if (userEl)  userEl.style.display  = 'flex';
      if (userNameEl) userNameEl.textContent = user.fullName?.split(' ')[0] || 'User';
      if (avatarEl) avatarEl.textContent = (user.fullName || 'U').charAt(0).toUpperCase();

      if (dashLink) {
        const dashMap = { ADMIN: '/admin-dashboard.html', DONOR: '/donor-dashboard.html', REQUESTER: '/requester-dashboard.html' };
        dashLink.href = dashMap[user.role] || '/index.html';
      }
    } else {
      if (guestEl) guestEl.style.display = 'flex';
      if (userEl)  userEl.style.display  = 'none';
    }
  },

  async logout() {
    try {
      await API.logout();
    } catch(e) {}
    Session.clear();
    Toast.success('Logged out successfully');
    setTimeout(() => window.location.href = '/index.html', 800);
  }
};

// =======================================================
// Reveal Animation on Scroll
// =======================================================
function initReveal() {
  const els = document.querySelectorAll('.reveal');
  if (!els.length) return;

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        e.target.classList.add('visible');
        observer.unobserve(e.target);
      }
    });
  }, { threshold: 0.12 });

  els.forEach(el => observer.observe(el));
}

// =======================================================
// Counter Animation
// =======================================================
function animateCounter(el, target, duration = 1500) {
  const start = 0;
  const step  = target / (duration / 16);
  let current = start;
  const timer = setInterval(() => {
    current = Math.min(current + step, target);
    el.textContent = Math.floor(current).toLocaleString();
    if (current >= target) clearInterval(timer);
  }, 16);
}

function initCounters() {
  const counters = document.querySelectorAll('[data-count]');
  if (!counters.length) return;
  const obs = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        const target = parseInt(e.target.dataset.count, 10);
        animateCounter(e.target, target);
        obs.unobserve(e.target);
      }
    });
  }, { threshold: 0.5 });
  counters.forEach(el => obs.observe(el));
}

// =======================================================
// Modal Utility
// =======================================================
const Modal = {
  open(id)  {
    const m = document.getElementById(id);
    if (m) {
      m.classList.add('active');
      document.body.style.overflow = 'hidden';
    }
  },
  close(id) {
    if (id) {
      const m = document.getElementById(id);
      if (m) m.classList.remove('active');
    } else {
      document.querySelectorAll('.modal-overlay.active').forEach(m => m.classList.remove('active'));
    }
    // Only restore scroll if no other modal is open
    if (!document.querySelector('.modal-overlay.active')) {
      document.body.style.overflow = '';
      document.body.style.overflowY = '';
      document.documentElement.style.overflow = '';
      document.documentElement.style.overflowY = 'auto';
    }
  },
  init() {
    document.querySelectorAll('[data-modal-close]').forEach(btn => {
      btn.addEventListener('click', () => {
        const id = btn.dataset.modalClose || btn.closest('.modal-overlay')?.id;
        this.close(id);
      });
    });
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
      overlay.addEventListener('click', e => {
        if (e.target === overlay) this.close(overlay.id);
      });
    });
    document.addEventListener('keydown', e => {
      if (e.key === 'Escape') this.close();
    });
  }
};

// =======================================================
// Blood Group Helpers
// =======================================================
const BLOOD_GROUPS = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

function bloodGroupBadge(bg) {
  return `<span class="blood-badge" title="Blood Group ${bg}">${bg}</span>`;
}

function urgencyBadge(level) {
  return `<span class="urgency-badge ${level}">${level}</span>`;
}

function statusBadge(status) {
  return `<span class="status-badge ${status}">${status}</span>`;
}

function availabilityBadge(avail) {
  return `<span class="availability-badge ${avail ? 'available' : 'unavailable'}">${avail ? 'Available' : 'Unavailable'}</span>`;
}

function formatDate(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}

function timeAgo(dateStr) {
  if (!dateStr) return '';
  const diff = Date.now() - new Date(dateStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1)   return 'just now';
  if (mins < 60)  return `${mins}m ago`;
  const hrs = Math.floor(mins / 60);
  if (hrs < 24)   return `${hrs}h ago`;
  const days = Math.floor(hrs / 24);
  if (days < 30)  return `${days}d ago`;
  return formatDate(dateStr);
}

function roleDashboard(role) {
  const map = { ADMIN: '/admin-dashboard.html', DONOR: '/donor-dashboard.html', REQUESTER: '/requester-dashboard.html' };
  return map[role] || '/index.html';
}

// =======================================================
// Form Helpers
// =======================================================
function setLoading(btn, loading, originalText) {
  if (loading) {
    btn.disabled = true;
    btn._origText = btn.innerHTML;
    btn.innerHTML = `<span class="spinner spinner-sm"></span> ${originalText || 'Processing...'}`;
  } else {
    btn.disabled = false;
    btn.innerHTML = btn._origText || btn.innerHTML;
  }
}

function clearValidation(form) {
  form.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
  form.querySelectorAll('.is-valid').forEach(el => el.classList.remove('is-valid'));
}

function markInvalid(input, message) {
  input.classList.add('is-invalid');
  input.classList.remove('is-valid');
  const err = input.nextElementSibling;
  if (err && err.classList.contains('form-error')) err.textContent = message;
}

function markValid(input) {
  input.classList.remove('is-invalid');
  input.classList.add('is-valid');
}

// =======================================================
// Dashboard Stats Loader
// =======================================================
async function loadDashboardStats() {
  try {
    const res = await API.getDashboardStats();
    if (res.success && res.data) {
      const d = res.data;
      const updateEl = (id, val) => {
        const el = document.getElementById(id);
        if (el) el.dataset.count = val, animateCounter(el, val);
      };
      updateEl('stat-total-users', d.totalUsers);
      updateEl('stat-total-donors', d.totalDonors);
      updateEl('stat-available-donors', d.availableDonors);
      updateEl('stat-total-requests', d.totalRequests);
      updateEl('stat-pending-requests', d.pendingRequests);
      updateEl('stat-fulfilled-requests', d.fulfilledRequests);
    }
  } catch(e) {
    console.warn('Could not load dashboard stats:', e.message);
  }
}

// =======================================================
// Init on DOM ready
// =======================================================
document.addEventListener('DOMContentLoaded', () => {
  Navbar.init();
  initReveal();
  initCounters();
  Modal.init();
});
