import { createContext, useContext, useState, useCallback } from 'react'
import api from '../api/apiClient'
import { useAuth } from './AuthContext'

const CartContext = createContext(null)

export function CartProvider({ children }) {
  // cart: { [prodId]: quantity }
  const [cart, setCart] = useState({})
  const { user } = useAuth()

  const totalItems = Object.values(cart).reduce((sum, qty) => sum + qty, 0)

  const addItem = useCallback(async (prodId) => {
    if (!user) return
    try {
      await api.post(`/inventory/cart/add/${prodId}`, null, {
        headers: { Authorization: `Bearer ${user.token}` },
      })
      setCart(prev => ({ ...prev, [prodId]: (prev[prodId] || 0) + 1 }))
    } catch (err) {
      if (err.response?.status === 409) alert('Item is out of stock!')
      else console.error('Add to cart failed:', err)
    }
  }, [user])

  const removeItem = useCallback(async (prodId) => {
    if (!user || !cart[prodId]) return
    try {
      await api.post(`/inventory/cart/remove/${prodId}`, null, {
        headers: { Authorization: `Bearer ${user.token}` },
      })
      setCart(prev => {
        const next = { ...prev }
        if (next[prodId] <= 1) delete next[prodId]
        else next[prodId] -= 1
        return next
      })
    } catch (err) {
      console.error('Remove from cart failed:', err)
    }
  }, [user, cart])

  const checkout = useCallback(async () => {
    if (!user || totalItems === 0) return false
    try {
      await api.post('/inventory/checkout', { items: cart }, {
        headers: { Authorization: `Bearer ${user.token}` },
      })
      setCart({})
      return true
    } catch (err) {
      console.error('Checkout failed:', err)
      return false
    }
  }, [user, cart, totalItems])

  const clearCart = () => setCart({})

  return (
    <CartContext.Provider value={{ cart, totalItems, addItem, removeItem, checkout, clearCart }}>
      {children}
    </CartContext.Provider>
  )
}

export const useCart = () => useContext(CartContext)
