import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'

export default function Navbar({ onSearch, onCategoryChange, categories, activeCategory, onCartOpen, onAuthOpen }) {
  const { user, logout } = useAuth()
  const { totalItems } = useCart()
  const [searchValue, setSearchValue] = useState('')

  const handleSearch = (e) => {
    const val = e.target.value
    setSearchValue(val)
    onSearch(val)
  }

  return (
    <header className="navbar">
      {/* Logo */}
      <div className="navbar-logo">
        <span className="logo-icon">🏪</span>
        <span className="logo-text">WarehouseMart</span>
      </div>

      {/* Search */}
      <div className="navbar-search">
        <span className="search-icon">🔍</span>
        <input
          type="text"
          className="search-input"
          placeholder="Search products, categories..."
          value={searchValue}
          onChange={handleSearch}
        />
      </div>

      {/* Right actions */}
      <div className="navbar-actions">
        {user ? (
          <>
            <span className="user-greeting">
              {user.role === 'ADMIN' ? '🛡️' : '👤'} {user.username}
            </span>
            <button className="nav-action-btn" onClick={logout} title="Sign out">
              Sign Out
            </button>
          </>
        ) : (
          <button className="nav-action-btn nav-action-primary" onClick={onAuthOpen}>
            Sign In
          </button>
        )}

        {/* Cart */}
        <button className="cart-btn-nav" onClick={onCartOpen} disabled={!user || user.role === 'ADMIN'}>
          🛒
          {totalItems > 0 && (
            <span className="cart-badge">{totalItems > 99 ? '99+' : totalItems}</span>
          )}
        </button>
      </div>

      {/* Category Nav */}
      <nav className="category-nav">
        <button
          className={`cat-pill ${!activeCategory ? 'active' : ''}`}
          onClick={() => onCategoryChange('')}
        >
          All Products
        </button>
        {(Array.isArray(categories) ? categories : []).map(cat => (
          <button
            key={cat}
            className={`cat-pill ${activeCategory === cat ? 'active' : ''}`}
            onClick={() => onCategoryChange(cat)}
          >
            {cat}
          </button>
        ))}
      </nav>
    </header>
  )
}
