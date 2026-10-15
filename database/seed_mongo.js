// MongoDB seed script — run via: mongosh warehouse_mongo_db seed_mongo.js
// Or use Docker entrypoint with --eval

db = db.getSiblingDB('warehouse_mongo_db');

db.product_details.createIndex({ prodId: 1 }, { unique: true });

db.product_details.insertMany([
  {
    prodId: 'PROD-001',
    description: 'The UltraBook Pro 15 delivers uncompromising performance in a feather-light chassis. Powered by Intel Core Ultra 9, 32GB LPDDR5X RAM, and a stunning 15.6" OLED display — built for creators and professionals.',
    brand: 'UltraTech',
    imageUrl: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600',
    specifications: { 'CPU': 'Intel Core Ultra 9-185H', 'RAM': '32GB LPDDR5X', 'Storage': '1TB NVMe SSD', 'Display': '15.6" 2.8K OLED', 'Battery': '86Wh, 20hr', 'Weight': '1.4 kg' },
    tags: ['laptop', 'ultrabook', 'intel', 'oled', 'professional']
  },
  {
    prodId: 'PROD-002',
    description: 'Industry-leading 40dB active noise cancellation. 30-hour battery life. Crystal-clear spatial audio with multipoint Bluetooth 5.3 connectivity. Folds flat for travel.',
    brand: 'SonicWave',
    imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600',
    specifications: { 'ANC': '40dB hybrid', 'Battery': '30 hours', 'Bluetooth': '5.3 Multipoint', 'Driver': '40mm dynamic', 'Weight': '250g', 'Codecs': 'LDAC, AAC, SBC' },
    tags: ['headphones', 'wireless', 'anc', 'audio', 'bluetooth']
  },
  {
    prodId: 'PROD-003',
    description: 'Immerse yourself in QLED 4K brilliance. Dolby Vision IQ and HDR10+ adaptive. Google TV with 120Hz gaming mode, HDMI 2.1, and built-in Alexa.',
    brand: 'VistaMax',
    imageUrl: 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=600',
    specifications: { 'Resolution': '4K UHD (3840×2160)', 'Panel': 'QLED', 'Refresh Rate': '120Hz (native)', 'HDR': 'Dolby Vision IQ, HDR10+', 'HDMI': '4× HDMI 2.1', 'OS': 'Google TV' },
    tags: ['tv', '4k', 'qled', 'smart-tv', 'dolby-vision']
  },
  {
    prodId: 'PROD-004',
    description: 'Per-key RGB illumination with Cherry MX Brown switches for a satisfying tactile bump. CNC aluminum top plate. N-key rollover, programmable macros, and USB passthrough.',
    brand: 'KeyForge',
    imageUrl: 'https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=600',
    specifications: { 'Switch': 'Cherry MX Brown (tactile)', 'Layout': 'TKL (87 keys)', 'Backlight': 'Per-key RGB', 'Construction': 'CNC Aluminium', 'Connection': 'USB-C detachable', 'Polling Rate': '1000Hz' },
    tags: ['keyboard', 'mechanical', 'rgb', 'cherry-mx', 'gaming']
  },
  {
    prodId: 'PROD-005',
    description: 'Transform any laptop into a full desktop workstation. Single-cable 140W PD charging, triple 4K display output, 2.5G Ethernet, and 7 USB ports.',
    brand: 'ConnectPro',
    imageUrl: 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=600',
    specifications: { 'Power Delivery': '140W USB-C PD', 'Video Out': '3× 4K @ 60Hz', 'Ethernet': '2.5 Gigabit', 'USB Ports': '7 (3×USB-A, 2×USB-C, 1×USB-C PD, 1×USB-C data)', 'SD Card': 'UHS-II', 'Compatibility': 'Windows, macOS, ChromeOS' },
    tags: ['dock', 'usb-c', 'productivity', 'workstation', 'accessories']
  },
  {
    prodId: 'PROD-006',
    description: 'Ruggedized pocket drive delivering 2,000MB/s read speeds. Military-grade drop resistance up to 3m. Works with USB-C and USB-A via included adapter.',
    brand: 'SwiftDrive',
    imageUrl: 'https://images.unsplash.com/photo-1601737487795-dab272f52420?w=600',
    specifications: { 'Capacity': '2TB', 'Interface': 'USB 3.2 Gen 2×2', 'Read Speed': '2,000 MB/s', 'Write Speed': '1,800 MB/s', 'Drop Resistance': '3m (MIL-STD-810G)', 'Encryption': 'AES-256' },
    tags: ['ssd', 'portable', 'storage', 'usb-c', 'rugged']
  },
  {
    prodId: 'PROD-007',
    description: 'Unify your smart home ecosystem. Supports Zigbee, Z-Wave, Thread, and Matter. Controls 100+ compatible devices with voice or the companion app. Local processing, no cloud dependency.',
    brand: 'NexusHome',
    imageUrl: 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600',
    specifications: { 'Protocols': 'Zigbee 3.0, Z-Wave Plus, Thread, Matter', 'Max Devices': '100+', 'Processing': 'Local (offline capable)', 'Assistant': 'Alexa, Google Home, HomeKit', 'Connectivity': 'Ethernet + Wi-Fi 6', 'Power': 'PoE or 12V DC' },
    tags: ['smart-home', 'hub', 'matter', 'zigbee', 'automation']
  },
  {
    prodId: 'PROD-008',
    description: 'Designed by ergonomists for 8-hour workdays. Adjustable lumbar support, 4D armrests, and breathable mesh back. Supports up to 150kg. 10-year warranty.',
    brand: 'ErgoElite',
    imageUrl: 'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?w=600',
    specifications: { 'Material': 'Breathable mesh + foam seat', 'Adjustments': 'Height, lumbar, 4D armrests, recline 90-135°', 'Max Load': '150 kg', 'Warranty': '10 years', 'Assembly': '15 minutes', 'Certifications': 'BIFMA, GREENGUARD Gold' },
    tags: ['chair', 'ergonomic', 'office', 'furniture', 'lumbar']
  },
  {
    prodId: 'PROD-009',
    description: 'Sony-sensor 4K webcam with AI-powered background blur and auto-framing. Streams at 60fps with HDR. Built-in privacy shutter and dual MEMS microphones.',
    brand: 'ClearCast',
    imageUrl: 'https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=600',
    specifications: { 'Resolution': '4K @ 30fps / 1080p @ 60fps', 'Sensor': 'Sony STARVIS CMOS', 'Microphone': 'Dual MEMS with noise cancellation', 'AI Features': 'Auto-framing, background blur, HDR', 'Connection': 'USB-C (USB 3.0)', 'Privacy': 'Physical shutter' },
    tags: ['webcam', '4k', 'streaming', 'video-call', 'peripherals']
  },
  {
    prodId: 'PROD-010',
    description: 'Wi-Fi 6E tri-band router with 6GHz support for ultra-low latency. OFDMA and MU-MIMO for simultaneous 4K streaming across 200+ devices. Includes WPA3 and built-in VPN server.',
    brand: 'NetBlaze',
    imageUrl: 'https://images.unsplash.com/photo-1606904825846-647eb07f5be2?w=600',
    specifications: { 'Standard': 'Wi-Fi 6E (802.11ax)', 'Bands': 'Tri-band: 2.4GHz + 5GHz + 6GHz', 'Max Speed': '10.8 Gbps (aggregate)', 'Ports': '1× 2.5G WAN, 4× 1G LAN, 1× USB 3.0', 'Security': 'WPA3, VPN server (OpenVPN, WireGuard)', 'Coverage': 'Up to 3,000 sq ft' },
    tags: ['router', 'wifi6e', 'networking', 'gaming', 'smart-home']
  }
]);

print('MongoDB seed complete: ' + db.product_details.countDocuments() + ' documents inserted.');
