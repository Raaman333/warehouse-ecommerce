import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import { formatINR, getWarehouseLabel } from '../utils/formatters'

export default function ProductCard({ product }) {
  const { user } = useAuth()
  const { cart, addItem, removeItem } = useCart()

  const {
    prodId,
    prodName,
    category,
    sellingPrice,
    totalStock,
    warehouses = [],
    suppliers = [],
    extraDetails,
  } = product

  const qty = cart[prodId] || 0
  const inStock = totalStock > 0
  const isCustomer = user && user.role === 'CUSTOMER'

  return (
    <article className="product-card">
      {/* Product image */}
      <div className="product-img-wrapper">
        <img
          src={`/products/${prodId}.jpg`}
          alt={prodName}
          className="product-img"
          loading="lazy"
          referrerPolicy="no-referrer"
          onError={e => {
            if (extraDetails?.imageUrl && e.target.src !== extraDetails.imageUrl) {
              e.target.src = extraDetails.imageUrl
            } else {
              e.target.style.display = 'none'
            }
          }}
        />
      </div>

      {/* Badges */}
      <div className="badge-row">
        <span className="badge badge-category">{category || 'General'}</span>
        {!inStock && <span className="badge badge-outofstock">Out of Stock</span>}
      </div>

      {/* Title */}
      <h3 className="product-title">{prodName}</h3>

      {/* Brand */}
      {extraDetails?.brand && (
        <p className="product-brand">by {extraDetails.brand}</p>
      )}

      {/* Description */}
      <p className="product-desc">
        {extraDetails?.description || 'No description available.'}
      </p>

      {/* Sold by & Ships from */}
      <div className="product-meta-lines">
        <p className="product-supplier">
          <strong>Sold by:</strong> {[...(suppliers || [])].join(', ') || 'Cloudtail India Ltd'}
        </p>
        {warehouses && warehouses.length > 0 && (
          <p className="product-warehouses">
            <strong>📍 Ships from:</strong> {[...(warehouses || [])].map(getWarehouseLabel).join(', ')}
          </p>
        )}
      </div>

      {/* Specs preview */}
      {extraDetails?.specifications && typeof extraDetails.specifications === 'object' && Object.keys(extraDetails.specifications).length > 0 && (
        <details className="specs-details">
          <summary>Key Specifications</summary>
          <ul className="specs-list">
            {Object.entries(extraDetails.specifications).slice(0, 4).map(([k, v]) => (
              <li key={k}><span className="spec-key">{k}:</span> {v}</li>
            ))}
          </ul>
        </details>
      )}

      {/* Footer */}
      <div className="product-footer">
        <div className="price-block">
          <span className="price-label">Price</span>
          <span className="price">{formatINR(sellingPrice)}</span>
        </div>
        <div className={`stock-indicator ${inStock ? 'in-stock' : 'out-of-stock'}`}>
          <span className="stock-dot"></span>
          <span>{inStock ? `${totalStock} in stock` : 'Out of stock'}</span>
        </div>
      </div>

      {/* Cart Controls — only for authenticated CUSTOMER */}
      {isCustomer && (
        <div className="cart-controls">
          <button
            className="cart-qty-btn minus"
            onClick={() => removeItem(prodId)}
            disabled={qty === 0}
            aria-label="Remove one"
          >
            −
          </button>
          <span className="cart-qty">{qty}</span>
          <button
            className="cart-qty-btn plus"
            onClick={() => addItem(prodId)}
            disabled={!inStock || qty >= totalStock}
            aria-label="Add one"
          >
            +
          </button>
        </div>
      )}

      {/* Sign-in prompt for guests */}
      {!user && (
        <p className="signin-prompt">Sign in to add to cart</p>
      )}
    </article>
  )
}
