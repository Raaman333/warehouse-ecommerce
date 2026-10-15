import { useState } from 'react'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'

export default function CartDrawer({ isOpen, onClose, products }) {
  const { cart, totalItems, removeItem, addItem, checkout } = useCart()
  const { user } = useAuth()
  const [checkoutState, setCheckoutState] = useState('idle') // idle | loading | success | error

  const cartItems = Object.entries(cart)
    .filter(([, qty]) => qty > 0)
    .map(([prodId, qty]) => {
      const product = products.find(p => p.prodId === prodId)
      return { prodId, qty, product }
    })

  const subtotal = cartItems.reduce((sum, { product, qty }) => {
    return sum + (product?.sellingPrice || 0) * qty
  }, 0)

  const handleCheckout = async () => {
    setCheckoutState('loading')
    const ok = await checkout()
    setCheckoutState(ok ? 'success' : 'error')
    if (ok) setTimeout(() => { setCheckoutState('idle'); onClose() }, 2000)
  }

  if (!isOpen) return null

  return (
    <>
      {/* Backdrop */}
      <div className="drawer-backdrop" onClick={onClose} />

      {/* Drawer */}
      <aside className="cart-drawer">
        <div className="drawer-header">
          <h2>🛒 Your Cart ({totalItems})</h2>
          <button className="drawer-close" onClick={onClose} aria-label="Close cart">✕</button>
        </div>

        {cartItems.length === 0 ? (
          <div className="cart-empty">
            <p>Your cart is empty.</p>
            <p className="cart-empty-sub">Browse our catalog and add items!</p>
          </div>
        ) : (
          <>
            <ul className="cart-items-list">
              {cartItems.map(({ prodId, qty, product }) => (
                <li key={prodId} className="cart-item">
                  <div className="cart-item-info">
                    <span className="cart-item-name">{product?.prodName || prodId}</span>
                    <span className="cart-item-price">
                      ${((product?.sellingPrice || 0) * qty).toFixed(2)}
                    </span>
                  </div>
                  <div className="cart-item-controls">
                    <button className="cart-qty-btn minus" onClick={() => removeItem(prodId)}>−</button>
                    <span className="cart-qty">{qty}</span>
                    <button
                      className="cart-qty-btn plus"
                      onClick={() => addItem(prodId)}
                      disabled={qty >= (product?.totalStock || 0)}
                    >+</button>
                  </div>
                </li>
              ))}
            </ul>

            <div className="cart-summary">
              <div className="cart-subtotal">
                <span>Subtotal ({totalItems} items)</span>
                <strong>${subtotal.toFixed(2)}</strong>
              </div>

              {checkoutState === 'success' && (
                <div className="checkout-success">✅ Order placed! Thank you.</div>
              )}
              {checkoutState === 'error' && (
                <div className="checkout-error">❌ Checkout failed. Please try again.</div>
              )}

              <button
                className="btn-checkout"
                onClick={handleCheckout}
                disabled={checkoutState === 'loading' || checkoutState === 'success'}
              >
                {checkoutState === 'loading' ? 'Processing...' : 'Place Order'}
              </button>
            </div>
          </>
        )}
      </aside>
    </>
  )
}
