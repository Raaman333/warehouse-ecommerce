import api from './apiClient'

// ─── Catalog ─────────────────────────────────────────────────────────────────

export const fetchProducts = (params = {}) =>
  api.get('/inventory/all', { params }).then(r => r.data)

export const fetchCategories = () =>
  api.get('/inventory/categories').then(r => r.data)

export const fetchProductDetail = (warehouseId, prodId) =>
  api.get(`/inventory/${warehouseId}/${prodId}`).then(r => r.data)

// ─── Auth ─────────────────────────────────────────────────────────────────────

export const loginUser = (credentials) =>
  api.post('/auth/login', credentials).then(r => r.data)

export const registerUser = (credentials) =>
  api.post('/auth/register', credentials).then(r => r.data)

// ─── Admin ────────────────────────────────────────────────────────────────────

export const addProduct = (token, payload) =>
  api.post('/inventory/add', payload, {
    headers: { Authorization: `Bearer ${token}` },
  }).then(r => r.data)

export const updateProduct = (token, payload) =>
  api.put('/inventory/update', payload, {
    headers: { Authorization: `Bearer ${token}` },
  })

export const deleteProduct = (token, warehouseId, prodId) =>
  api.delete(`/inventory/delete/${warehouseId}/${prodId}`, {
    headers: { Authorization: `Bearer ${token}` },
  })

export const fetchSuppliers = () =>
  api.get('/inventory/suppliers').then(r => r.data)

export const fetchWarehouses = () =>
  api.get('/inventory/warehouses').then(r => r.data)
