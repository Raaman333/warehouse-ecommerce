/**
 * Indian Rupee (INR) and Indian warehouse formatting utilities
 */

export function formatINR(val) {
  if (val === null || val === undefined || isNaN(val)) return '₹0'
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(val)
}

export const WAREHOUSE_NAMES = {
  1: 'Mumbai Hub',
  2: 'Bengaluru Park',
  3: 'Delhi NCR Depot',
  4: 'Hyderabad Hub',
  5: 'Chennai Depot',
  6: 'Kolkata Hub',
}

export function getWarehouseLabel(id) {
  return WAREHOUSE_NAMES[id] || `Warehouse ${id}`
}
