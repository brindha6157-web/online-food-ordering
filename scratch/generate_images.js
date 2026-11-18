const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

const projectRoot = 'C:/Users/ELCOT/.gemini/antigravity/scratch/FoodHub';

const catDirs = [
    path.join(projectRoot, 'frontend/images/categories'),
    path.join(projectRoot, 'backend/src/main/resources/static/images/categories')
];
const menuDirs = [
    path.join(projectRoot, 'frontend/images/menu'),
    path.join(projectRoot, 'backend/src/main/resources/static/images/menu')
];

[...catDirs, ...menuDirs].forEach(d => {
    if (!fs.existsSync(d)) fs.mkdirSync(d, { recursive: true });
});

// CRC32 and PNG helpers
const crcTable = [];
for (let n = 0; n < 256; n++) {
    let c = n;
    for (let k = 0; k < 8; k++) c = ((c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1));
    crcTable[n] = c;
}
function crc32(buf) {
    let crc = 0 ^ (-1);
    for (let i = 0; i < buf.length; i++) crc = (crc >>> 8) ^ crcTable[(crc ^ buf[i]) & 0xFF];
    return (crc ^ (-1)) >>> 0;
}
function createChunk(type, data) {
    const len = Buffer.alloc(4); len.writeUInt32BE(data.length, 0);
    const typeBuf = Buffer.from(type);
    const crcBuf = Buffer.alloc(4); crcBuf.writeUInt32BE(crc32(Buffer.concat([typeBuf, data])) >>> 0, 0);
    return Buffer.concat([len, typeBuf, data, crcBuf]);
}
function createPng(width, height, color) {
    const signature = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);
    const ihdr = Buffer.alloc(13);
    ihdr.writeUInt32BE(width, 0); ihdr.writeUInt32BE(height, 4);
    ihdr.writeUInt8(8, 8); ihdr.writeUInt8(2, 9); // RGB
    ihdr.writeUInt8(0, 10); ihdr.writeUInt8(0, 11); ihdr.writeUInt8(0, 12);
    const ihdrChunk = createChunk('IHDR', ihdr);

    const scanline = width * 3 + 1;
    const raw = Buffer.alloc(height * scanline);
    for (let y = 0; y < height; y++) {
        raw[y * scanline] = 0;
        for (let x = 0; x < width; x++) {
            const pos = y * scanline + 1 + x * 3;
            raw[pos] = color[0]; raw[pos+1] = color[1]; raw[pos+2] = color[2];
        }
    }
    const idatChunk = createChunk('IDAT', zlib.deflateSync(raw));
    const iendChunk = createChunk('IEND', Buffer.alloc(0));
    return Buffer.concat([signature, ihdrChunk, idatChunk, iendChunk]);
}

// Generate Category Images
const categories = [
    { name: 'pizza', label: 'Pizza', icon: '🍕', color: [230, 81, 0] },
    { name: 'burger', label: 'Burger', icon: '🍔', color: [194, 65, 12] },
    { name: 'biryani', label: 'Biryani', icon: '🍚', color: [217, 119, 6] },
    { name: 'drinks', label: 'Drinks', icon: '🍹', color: [2, 132, 199] },
    { name: 'desserts', label: 'Desserts', icon: '🍰', color: [192, 38, 211] }
];

categories.forEach(cat => {
    const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 300" width="100%" height="100%">
  <defs>
    <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#ff7043"/>
      <stop offset="100%" stop-color="#d84315"/>
    </linearGradient>
  </defs>
  <rect width="400" height="300" fill="url(#grad)" rx="16"/>
  <circle cx="200" cy="130" r="70" fill="rgba(255,255,255,0.2)"/>
  <text x="200" y="155" font-size="80" text-anchor="middle">${cat.icon}</text>
  <text x="200" y="240" font-family="Segoe UI, sans-serif" font-size="28" font-weight="bold" fill="#ffffff" text-anchor="middle">${cat.label}</text>
</svg>`;

    catDirs.forEach(d => {
        fs.writeFileSync(path.join(d, `${cat.name}.svg`), svg, 'utf8');
        fs.writeFileSync(path.join(d, `${cat.name}.jpg`), createPng(100, 100, cat.color));
    });
});

// Generate Menu Items
const menuItems = [
    { name: 'margherita', label: 'Margherita Supreme', icon: '🍕', color: [230, 81, 0] },
    { name: 'paneer-pizza', label: 'Paneer Tikka Pizza', icon: '🍕', color: [230, 81, 0] },
    { name: 'chicken-pizza', label: 'Chicken Feast Pizza', icon: '🍕', color: [230, 81, 0] },
    { name: 'veggie-pizza', label: 'Veggie Paradise', icon: '🍕', color: [230, 81, 0] },
    { name: 'veg-burger', label: 'Classic Veg Burger', icon: '🍔', color: [194, 65, 12] },
    { name: 'chicken-burger', label: 'Grilled Chicken Burger', icon: '🍔', color: [194, 65, 12] },
    { name: 'cheese-burger', label: 'Double Cheese Burger', icon: '🍔', color: [194, 65, 12] },
    { name: 'paneer-burger', label: 'Spicy Paneer Burger', icon: '🍔', color: [194, 65, 12] },
    { name: 'chicken-biryani', label: 'Chicken Dum Biryani', icon: '🍗', color: [217, 119, 6] },
    { name: 'mutton-biryani', label: 'Mutton Royal Biryani', icon: '🍖', color: [217, 119, 6] },
    { name: 'veg-biryani', label: 'Special Veg Biryani', icon: '🍲', color: [217, 119, 6] },
    { name: 'egg-biryani', label: 'Egg Biryani Feast', icon: '🍳', color: [217, 119, 6] },
    { name: 'mojito', label: 'Fresh Mint Mojito', icon: '🍸', color: [2, 132, 199] },
    { name: 'mango-lassi', label: 'Mango Lassi', icon: '🥤', color: [234, 179, 8] },
    { name: 'cold-coffee', label: 'Iced Cold Coffee', icon: '☕', color: [120, 53, 15] },
    { name: 'brownie', label: 'Chocolate Brownie', icon: '🍫', color: [88, 28, 135] },
    { name: 'gulab-jamun', label: 'Gulab Jamun Ice Cream', icon: '🍨', color: [190, 24, 93] },
    { name: 'kulfi', label: 'Royal Kulfi Falooda', icon: '🍧', color: [192, 38, 211] },
    { name: 'default', label: 'FoodHub Special', icon: '🍽️', color: [230, 81, 0] }
];

menuItems.forEach(item => {
    const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 300" width="100%" height="100%">
  <defs>
    <linearGradient id="itemGrad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#fff7ed"/>
      <stop offset="100%" stop-color="#ffedd5"/>
    </linearGradient>
  </defs>
  <rect width="400" height="300" fill="url(#itemGrad)" rx="16"/>
  <rect x="0" y="0" width="400" height="300" fill="none" stroke="#fed7aa" stroke-width="3" rx="16"/>
  <circle cx="200" cy="120" r="65" fill="#ea580c"/>
  <text x="200" y="145" font-size="70" text-anchor="middle">${item.icon}</text>
  <text x="200" y="235" font-family="Segoe UI, sans-serif" font-size="22" font-weight="bold" fill="#1f2937" text-anchor="middle">${item.label}</text>
  <text x="200" y="265" font-family="Segoe UI, sans-serif" font-size="14" fill="#9a3412" text-anchor="middle">FoodHub Chef Special</text>
</svg>`;

    menuDirs.forEach(d => {
        fs.writeFileSync(path.join(d, `${item.name}.svg`), svg, 'utf8');
        fs.writeFileSync(path.join(d, `${item.name}.jpg`), createPng(100, 100, item.color));
    });
});

console.log('All food images and category graphics generated successfully!');
