import { useState, useEffect, useCallback } from 'react'
import { useAuth } from '../context/AuthContext'
import { formatINR, getWarehouseLabel } from '../utils/formatters'
import {
  fetchSuppliers,
  fetchWarehouses,
  addProduct,
  updateProduct,
  deleteProduct,
  fetchProducts,
} from '../api/inventoryService'

const EMPTY_PRODUCT = {
  warehouseId: '', prodId: '', prodName: '', stockNumber: '', sellingPrice: '',
  supplierId: '', category: '',
}
const EMPTY_DETAILS = { description: '', brand: '', imageUrl: '', specifications: '' }

export default function AdminPanel() {
  const { user } = useAuth()
  const [tab, setTab] = useState('add') // add | update | delete | view
  const [suppliers, setSuppliers] = useState([])
  const [warehouses, setWarehouses] = useState([])
  const [products, setProducts] = useState([])
  const [form, setForm] = useState(EMPTY_PRODUCT)
  const [details, setDetails] = useState(EMPTY_DETAILS)
  const [message, setMessage] = useState(null) // { type: 'success'|'error', text }
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    fetchSuppliers().then(d => setSuppliers(Array.isArray(d) ? d : [])).catch(console.error)
    fetchWarehouses().then(d => setWarehouses(Array.isArray(d) ? d : [])).catch(console.error)
    fetchProducts().then(d => setProducts(Array.isArray(d) ? d : [])).catch(console.error)
  }, [])

  const notify = (type, text) => {
    setMessage({ type, text })
    setTimeout(() => setMessage(null), 4000)
  }

  const buildPayload = () => ({
    inventoryData: {
      warehouseId: Number(form.warehouseId),
      prodId: form.prodId,
      prodName: form.prodName,
      stockNumber: Number(form.stockNumber),
      sellingPrice: parseFloat(form.sellingPrice),
      supplierId: Number(form.supplierId),
      category: form.category,
    },
    extraDetails: {
      description: details.description,
      brand: details.brand,
      imageUrl: details.imageUrl,
      specifications: details.specifications
        ? Object.fromEntries(details.specifications.split(',').map(p => {
            const [k, ...v] = p.split(':')
            return [k?.trim(), v.join(':').trim()]
          }).filter(([k]) => k))
        : {},
    },
  })

  const handleAdd = async e => {
    e.preventDefault()
    setLoading(true)
    try {
      await addProduct(user.token, buildPayload())
      notify('success', `✅ Product "${form.prodName}" added successfully.`)
      setForm(EMPTY_PRODUCT)
      setDetails(EMPTY_DETAILS)
      const refreshed = await fetchProducts()
      setProducts(refreshed)
    } catch (err) {
      notify('error', `❌ ${err.response?.data || 'Failed to add product.'}`)
    } finally { setLoading(false) }
  }

  const handleUpdate = async e => {
    e.preventDefault()
    setLoading(true)
    try {
      await updateProduct(user.token, buildPayload())
      notify('success', `✅ Product "${form.prodId}" updated.`)
      const refreshed = await fetchProducts()
      setProducts(refreshed)
    } catch (err) {
      notify('error', `❌ ${err.response?.data || 'Update failed.'}`)
    } finally { setLoading(false) }
  }

  const handleDelete = async e => {
    e.preventDefault()
    if (!window.confirm(`Delete ${form.prodId} from warehouse ${form.warehouseId}?`)) return
    setLoading(true)
    try {
      await deleteProduct(user.token, form.warehouseId, form.prodId)
      notify('success', `✅ Product "${form.prodId}" deleted.`)
      const refreshed = await fetchProducts()
      setProducts(refreshed)
    } catch (err) {
      notify('error', `❌ ${err.response?.data || 'Delete failed.'}`)
    } finally { setLoading(false) }
  }

  const f = (k) => e => setForm(p => ({ ...p, [k]: e.target.value }))
  const d = (k) => e => setDetails(p => ({ ...p, [k]: e.target.value }))

  const WarehouseSelect = () => (
    <select className="form-input" value={form.warehouseId} onChange={f('warehouseId')} required>
      <option value="">Select Warehouse</option>
      {warehouses.map(w => <option key={w.warehouseId} value={w.warehouseId}>{w.name} – {w.location}</option>)}
    </select>
  )

  const SupplierSelect = () => (
    <select className="form-input" value={form.supplierId} onChange={f('supplierId')}>
      <option value="">No supplier</option>
      {suppliers.map(s => <option key={s.supplierId} value={s.supplierId}>{s.name}</option>)}
    </select>
  )

  return (
    <section className="admin-panel">
      <div className="admin-header">
        <h2>🛡️ Admin — Inventory Management</h2>
        <p className="admin-sub">Manage warehouse stock, pricing, and product metadata.</p>
      </div>

      {/* Tab nav */}
      <div className="admin-tabs">
        {['add', 'update', 'delete', 'view'].map(t => (
          <button key={t} className={`admin-tab ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>
            {{ add: '➕ Add', update: '✏️ Update', delete: '🗑️ Delete', view: '📋 View All' }[t]}
          </button>
        ))}
      </div>

      {/* Feedback message */}
      {message && (
        <div className={`admin-message ${message.type}`}>{message.text}</div>
      )}

      {/* ADD */}
      {tab === 'add' && (
        <form className="admin-form" onSubmit={handleAdd}>
          <div className="form-grid">
            <div className="form-group"><label>Warehouse *</label><WarehouseSelect /></div>
            <div className="form-group"><label>Product SKU *</label>
              <input className="form-input" placeholder="e.g. PROD-011" value={form.prodId} onChange={f('prodId')} required />
            </div>
            <div className="form-group"><label>Product Name *</label>
              <input className="form-input" placeholder="Product name" value={form.prodName} onChange={f('prodName')} required />
            </div>
            <div className="form-group"><label>Category</label>
              <input className="form-input" placeholder="e.g. Laptops" value={form.category} onChange={f('category')} />
            </div>
            <div className="form-group"><label>Stock Qty *</label>
              <input className="form-input" type="number" min="0" value={form.stockNumber} onChange={f('stockNumber')} required />
            </div>
            <div className="form-group"><label>Price (₹ INR) *</label>
              <input className="form-input" type="number" step="1" min="0" value={form.sellingPrice} onChange={f('sellingPrice')} required />
            </div>
            <div className="form-group"><label>Supplier</label><SupplierSelect /></div>
            <div className="form-group"><label>Brand</label>
              <input className="form-input" placeholder="Brand name" value={details.brand} onChange={d('brand')} />
            </div>
            <div className="form-group form-group-wide"><label>Image URL</label>
              <input className="form-input" placeholder="https://..." value={details.imageUrl} onChange={d('imageUrl')} />
            </div>
            <div className="form-group form-group-wide"><label>Description</label>
              <textarea className="form-input" rows={3} placeholder="Product description..." value={details.description} onChange={d('description')} />
            </div>
            <div className="form-group form-group-wide">
              <label>Specifications <small>(key: value, comma-separated)</small></label>
              <input className="form-input" placeholder="RAM: 16GB, Storage: 512GB" value={details.specifications} onChange={d('specifications')} />
            </div>
          </div>
          <button type="submit" className="btn-admin-submit" disabled={loading}>
            {loading ? 'Adding...' : 'Add Product'}
          </button>
        </form>
      )}

      {/* UPDATE */}
      {tab === 'update' && (
        <form className="admin-form" onSubmit={handleUpdate}>
          <div className="form-grid">
            <div className="form-group"><label>Warehouse *</label><WarehouseSelect /></div>
            <div className="form-group"><label>Product SKU *</label>
              <input className="form-input" placeholder="Exact SKU to update" value={form.prodId} onChange={f('prodId')} required />
            </div>
            <div className="form-group"><label>New Name</label>
              <input className="form-input" placeholder="Leave blank to keep" value={form.prodName} onChange={f('prodName')} />
            </div>
            <div className="form-group"><label>New Category</label>
              <input className="form-input" placeholder="Leave blank to keep" value={form.category} onChange={f('category')} />
            </div>
            <div className="form-group"><label>New Stock Qty</label>
              <input className="form-input" type="number" min="0" placeholder="Leave blank to keep" value={form.stockNumber} onChange={f('stockNumber')} />
            </div>
            <div className="form-group"><label>New Price (₹ INR)</label>
              <input className="form-input" type="number" step="1" min="0" placeholder="Leave blank to keep" value={form.sellingPrice} onChange={f('sellingPrice')} />
            </div>
            <div className="form-group"><label>New Supplier</label><SupplierSelect /></div>
            <div className="form-group form-group-wide"><label>New Description</label>
              <textarea className="form-input" rows={2} placeholder="Leave blank to keep" value={details.description} onChange={d('description')} />
            </div>
          </div>
          <button type="submit" className="btn-admin-submit btn-warning" disabled={loading}>
            {loading ? 'Updating...' : 'Apply Update'}
          </button>
        </form>
      )}

      {/* DELETE */}
      {tab === 'delete' && (
        <form className="admin-form" onSubmit={handleDelete}>
          <div className="form-grid">
            <div className="form-group"><label>Warehouse *</label><WarehouseSelect /></div>
            <div className="form-group"><label>Product SKU *</label>
              <input className="form-input" placeholder="Exact SKU to delete" value={form.prodId} onChange={f('prodId')} required />
            </div>
          </div>
          <div className="delete-warning">
            ⚠️ This permanently removes the product from this warehouse.
            If no other warehouses stock it, MongoDB details will also be deleted.
          </div>
          <button type="submit" className="btn-admin-submit btn-danger" disabled={loading}>
            {loading ? 'Deleting...' : 'Confirm Delete'}
          </button>
        </form>
      )}

      {/* VIEW ALL */}
      {tab === 'view' && (
        <div className="admin-table-wrap">
          <table className="admin-table">
            <thead>
              <tr>
                <th>SKU</th>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Total Stock</th>
                <th>Warehouses</th>
              </tr>
            </thead>
            <tbody>
              {products.map(p => (
                <tr key={p.prodId}>
                  <td><code>{p.prodId}</code></td>
                  <td>{p.prodName}</td>
                  <td>{p.category}</td>
                  <td>{formatINR(p.sellingPrice)}</td>
                  <td className={p.totalStock < 5 ? 'low-stock' : ''}>{p.totalStock}</td>
                  <td>{[...(p.warehouses || [])].map(getWarehouseLabel).join(', ')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
