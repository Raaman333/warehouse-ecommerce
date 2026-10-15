import { useState, useEffect, useCallback, useDeferredValue } from 'react'
import { useAuth } from './context/AuthContext'
import { fetchProducts, fetchCategories } from './api/inventoryService'
import Navbar from './components/Navbar'
import ProductCard from './components/ProductCard'
import CartDrawer from './components/CartDrawer'
import AuthModal from './components/AuthModal'
import AdminPanel from './pages/AdminPanel'
import './index.css'

export default function App() {
  const { user } = useAuth()
  const [products, setProducts]         = useState([])
  const [categories, setCategories]     = useState([])
  const [loading, setLoading]           = useState(true)
  const [error, setError]               = useState(null)
  const [search, setSearch]             = useState('')
  const [activeCategory, setCategory]   = useState('')
  const [cartOpen, setCartOpen]         = useState(false)
  const [authOpen, setAuthOpen]         = useState(false)

  // Deferred search avoids blocking render on each keystroke
  const deferredSearch = useDeferredValue(search)

  const loadProducts = useCallback(async (params) => {
    setLoading(true)
    setError(null)
    try {
      const data = await fetchProducts(params)
      setProducts(Array.isArray(data) ? data : [])
    } catch (err) {
      setError('Failed to load products. Is the backend running?')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [])

  // Initial load
  useEffect(() => {
    fetchCategories()
      .then(data => setCategories(Array.isArray(data) ? data : []))
      .catch(console.error)
    loadProducts({})
  }, [loadProducts])

  // Reload when search or category changes
  useEffect(() => {
    const params = {}
    if (deferredSearch) params.search = deferredSearch
    else if (activeCategory) params.category = activeCategory
    loadProducts(params)
  }, [deferredSearch, activeCategory, loadProducts])

  const handleSearch = (val) => {
    setSearch(val)
    if (val) setCategory('') // Clear category filter when searching
  }

  const handleCategoryChange = (cat) => {
    setCategory(cat)
    setSearch('')
  }

  // Show admin panel for admin users
  const showAdmin = user?.role === 'ADMIN'

  return (
    <div className="app-layout">
      <Navbar
        onSearch={handleSearch}
        onCategoryChange={handleCategoryChange}
        categories={categories}
        activeCategory={activeCategory}
        onCartOpen={() => setCartOpen(true)}
        onAuthOpen={() => setAuthOpen(true)}
      />

      <main className="main-content">
        {showAdmin ? (
          <AdminPanel />
        ) : (
          <>
            {/* Hero banner */}
            {!search && !activeCategory && (
              <div className="hero-banner">
                <div className="hero-text">
                  <h1>Welcome to <span className="hero-brand">WarehouseMart</span></h1>
                  <p>Discover thousands of products from our multi-warehouse network — powered by a polyglot cloud architecture.</p>
                </div>
                {!user && (
                  <button className="hero-cta" onClick={() => setAuthOpen(true)}>
                    Sign In to Shop
                  </button>
                )}
              </div>
            )}

            {/* Results header */}
            <div className="results-header">
              <h2 className="results-title">
                {search
                  ? `Search results for "${search}"`
                  : activeCategory
                    ? activeCategory
                    : 'All Products'}
              </h2>
              <span className="results-count">
                {loading ? '...' : `${products.length} item${products.length !== 1 ? 's' : ''}`}
              </span>
            </div>

            {/* Loading skeleton */}
            {loading && (
              <div className="product-grid">
                {Array.from({ length: 6 }).map((_, i) => (
                  <div key={i} className="product-card skeleton" aria-hidden="true">
                    <div className="skeleton-img" />
                    <div className="skeleton-line" />
                    <div className="skeleton-line short" />
                    <div className="skeleton-line" />
                  </div>
                ))}
              </div>
            )}

            {/* Error state */}
            {error && !loading && (
              <div className="error-state">
                <p>⚠️ {error}</p>
                <button onClick={() => loadProducts({})} className="btn-retry">Retry</button>
              </div>
            )}

            {/* Product grid */}
            {!loading && !error && (
              <>
                {products.length === 0 ? (
                  <div className="empty-state">
                    <p>🔍 No products found.</p>
                    <button onClick={() => { setSearch(''); setCategory(''); loadProducts({}) }}>
                      Clear filters
                    </button>
                  </div>
                ) : (
                  <div className="product-grid">
                    {(Array.isArray(products) ? products : []).map(product => (
                      <ProductCard key={product.prodId} product={product} />
                    ))}
                  </div>
                )}
              </>
            )}
          </>
        )}
      </main>

      <CartDrawer
        isOpen={cartOpen}
        onClose={() => setCartOpen(false)}
        products={products}
      />

      <AuthModal
        isOpen={authOpen}
        onClose={() => setAuthOpen(false)}
      />
    </div>
  )
}
