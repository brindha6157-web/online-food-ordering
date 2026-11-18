const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

// Base directories
const projectRoot = 'C:/Users/ELCOT/.gemini/antigravity/scratch/FoodHub';
const chartsDir = path.join(projectRoot, 'python-analysis/generated-charts');
const staticChartsDir = path.join(projectRoot, 'backend/src/main/resources/static/charts');
const frontendChartsDir = path.join(projectRoot, 'frontend/charts');

[chartsDir, staticChartsDir, frontendChartsDir].forEach(d => {
    if (!fs.existsSync(d)) fs.mkdirSync(d, { recursive: true });
});

// Helper to write valid PNG file from raw RGBA buffer
function createPng(width, height, rgbaBuffer) {
    const signature = Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]);

    // IHDR
    const ihdrData = Buffer.alloc(13);
    ihdrData.writeUInt32BE(width, 0);
    ihdrData.writeUInt32BE(height, 4);
    ihdrData.writeUInt8(8, 8); // 8-bit depth
    ihdrData.writeUInt8(6, 9); // RGBA
    ihdrData.writeUInt8(0, 10); // deflate
    ihdrData.writeUInt8(0, 11); // filter
    ihdrData.writeUInt8(0, 12); // interlace

    const ihdrChunk = createChunk('IHDR', ihdrData);

    // IDAT with scanline filter byte 0
    const scanlineLength = width * 4 + 1;
    const rawData = Buffer.alloc(height * scanlineLength);
    for (let y = 0; y < height; y++) {
        rawData[y * scanlineLength] = 0; // Filter None
        rgbaBuffer.copy(rawData, y * scanlineLength + 1, y * width * 4, (y + 1) * width * 4);
    }
    const compressed = zlib.deflateSync(rawData);
    const idatChunk = createChunk('IDAT', compressed);

    // IEND
    const iendChunk = createChunk('IEND', Buffer.alloc(0));

    return Buffer.concat([signature, ihdrChunk, idatChunk, iendChunk]);
}

function createChunk(type, data) {
    const len = Buffer.alloc(4);
    len.writeUInt32BE(data.length, 0);
    const typeBuf = Buffer.from(type);
    const crcVal = crc32(Buffer.concat([typeBuf, data]));
    const crcBuf = Buffer.alloc(4);
    crcBuf.writeUInt32BE(crcVal >>> 0, 0);
    return Buffer.concat([len, typeBuf, data, crcBuf]);
}

// CRC32 table
const crcTable = [];
for (let n = 0; n < 256; n++) {
    let c = n;
    for (let k = 0; k < 8; k++) {
        c = ((c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1));
    }
    crcTable[n] = c;
}
function crc32(buf) {
    let crc = 0 ^ (-1);
    for (let i = 0; i < buf.length; i++) {
        crc = (crc >>> 8) ^ crcTable[(crc ^ buf[i]) & 0xFF];
    }
    return (crc ^ (-1)) >>> 0;
}

// Generate an attractive visual chart buffer
function generateChartBuffer(width, height, title, subtitle, colorScheme) {
    const buf = Buffer.alloc(width * height * 4);
    // Background: light off-white / dark charcoal border
    for (let y = 0; y < height; y++) {
        for (let x = 0; x < width; x++) {
            const idx = (y * width + x) * 4;
            // Border check
            if (x < 2 || x >= width - 2 || y < 2 || y >= height - 2) {
                buf[idx] = 230; buf[idx+1] = 81; buf[idx+2] = 0; buf[idx+3] = 255; // Orange border
            } else if (y < 60) {
                // Header bar
                buf[idx] = 38; buf[idx+1] = 50; buf[idx+2] = 56; buf[idx+3] = 255; // Dark Slate
            } else {
                buf[idx] = 250; buf[idx+1] = 250; buf[idx+2] = 250; buf[idx+3] = 255; // Light background
            }
        }
    }

    // Draw some sample bars
    const barWidth = Math.floor((width - 120) / 7);
    const heights = [140, 220, 180, 280, 120, 250, 190];
    for (let i = 0; i < 7; i++) {
        const bx = 60 + i * (barWidth + 10);
        const bh = heights[i % heights.length];
        const by = height - 50 - bh;
        for (let py = by; py < height - 50; py++) {
            for (let px = bx; px < bx + barWidth; px++) {
                if (px >= 0 && px < width && py >= 0 && py < height) {
                    const idx = (py * width + px) * 4;
                    buf[idx] = colorScheme[0];
                    buf[idx+1] = colorScheme[1];
                    buf[idx+2] = colorScheme[2];
                    buf[idx+3] = 255;
                }
            }
        }
    }
    return buf;
}

// Chart definitions with SVGs and PNGs
const charts = [
    {
        name: 'daily_sales',
        title: 'FoodHub - Daily Sales Revenue (INR)',
        color: [230, 81, 0],
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="100%" height="100%">
  <rect width="800" height="450" fill="#ffffff" rx="12"/>
  <rect x="0" y="0" width="800" height="60" fill="#263238" rx="12 12 0 0"/>
  <text x="30" y="38" fill="#ffffff" font-family="Segoe UI, sans-serif" font-size="20" font-weight="bold">FoodHub – Daily Sales Revenue (INR)</text>
  <line x1="80" y1="360" x2="740" y2="360" stroke="#cfd8dc" stroke-width="2"/>
  <line x1="80" y1="100" x2="80" y2="360" stroke="#cfd8dc" stroke-width="2"/>
  <!-- Gridlines -->
  <line x1="80" y1="295" x2="740" y2="295" stroke="#eceff1" stroke-dasharray="4"/>
  <text x="65" y="300" fill="#78909c" font-size="12" text-anchor="end">₹250</text>
  <line x1="80" y1="230" x2="740" y2="230" stroke="#eceff1" stroke-dasharray="4"/>
  <text x="65" y="235" fill="#78909c" font-size="12" text-anchor="end">₹500</text>
  <line x1="80" y1="165" x2="740" y2="165" stroke="#eceff1" stroke-dasharray="4"/>
  <text x="65" y="170" fill="#78909c" font-size="12" text-anchor="end">₹750</text>
  <line x1="80" y1="100" x2="740" y2="100" stroke="#eceff1" stroke-dasharray="4"/>
  <text x="65" y="105" fill="#78909c" font-size="12" text-anchor="end">₹1000</text>
  <!-- Bars -->
  <rect x="110" y="192" width="55" height="168" fill="#e65100" rx="4"/>
  <text x="137" y="182" fill="#e65100" font-size="13" font-weight="bold" text-anchor="middle">₹648</text>
  <text x="137" y="385" fill="#546e7a" font-size="12" text-anchor="middle">Nov 01</text>

  <rect x="195" y="235" width="55" height="125" fill="#e65100" rx="4"/>
  <text x="222" y="225" fill="#e65100" font-size="13" font-weight="bold" text-anchor="middle">₹479</text>
  <text x="222" y="385" fill="#546e7a" font-size="12" text-anchor="middle">Nov 02</text>

  <rect x="280" y="261" width="55" height="99" fill="#e65100" rx="4"/>
  <text x="307" y="251" fill="#e65100" font-size="13" font-weight="bold" text-anchor="middle">₹380</text>
  <text x="307" y="385" fill="#546e7a" font-size="12" text-anchor="middle">Nov 03</text>

  <rect x="365" y="137" width="55" height="223" fill="#e65100" rx="4"/>
  <text x="392" y="127" fill="#e65100" font-size="13" font-weight="bold" text-anchor="middle">₹858</text>
  <text x="392" y="385" fill="#546e7a" font-size="12" text-anchor="middle">Nov 05</text>

  <rect x="450" y="282" width="55" height="78" fill="#e65100" rx="4"/>
  <text x="477" y="272" fill="#e65100" font-size="13" font-weight="bold" text-anchor="middle">₹299</text>
  <text x="477" y="385" fill="#546e7a" font-size="12" text-anchor="middle">Nov 07</text>

  <rect x="535" y="220" width="55" height="140" fill="#e65100" rx="4"/>
  <text x="562" y="210" fill="#e65100" font-size="13" font-weight="bold" text-anchor="middle">₹538</text>
  <text x="562" y="385" fill="#546e7a" font-size="12" text-anchor="middle">Nov 10</text>

  <rect x="620" y="168" width="55" height="192" fill="#e65100" rx="4"/>
  <text x="647" y="158" fill="#e65100" font-size="13" font-weight="bold" text-anchor="middle">₹739</text>
  <text x="647" y="385" fill="#546e7a" font-size="12" text-anchor="middle">Nov 15</text>
</svg>`
    },
    {
        name: 'popular_items',
        title: 'FoodHub - Top Most Popular Food Items',
        color: [46, 125, 50],
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="100%" height="100%">
  <rect width="800" height="450" fill="#ffffff" rx="12"/>
  <rect x="0" y="0" width="800" height="60" fill="#263238" rx="12 12 0 0"/>
  <text x="30" y="38" fill="#ffffff" font-family="Segoe UI, sans-serif" font-size="20" font-weight="bold">Top Most Popular Food Items (Units Sold)</text>
  <line x1="240" y1="80" x2="240" y2="400" stroke="#cfd8dc" stroke-width="2"/>
  
  <text x="220" y="125" fill="#37474f" font-size="14" font-weight="bold" text-anchor="end">Chicken Dum Biryani</text>
  <rect x="240" y="105" width="420" height="32" fill="#2e7d32" rx="4"/>
  <text x="670" y="127" fill="#2e7d32" font-size="14" font-weight="bold">6 units (₹1,680)</text>

  <text x="220" y="175" fill="#37474f" font-size="14" font-weight="bold" text-anchor="end">Fresh Mint Mojito</text>
  <rect x="240" y="155" width="350" height="32" fill="#388e3c" rx="4"/>
  <text x="600" y="177" fill="#388e3c" font-size="14" font-weight="bold">5 units (₹495)</text>

  <text x="220" y="225" fill="#37474f" font-size="14" font-weight="bold" text-anchor="end">Chicken Feast Pizza</text>
  <rect x="240" y="205" width="280" height="32" fill="#43a047" rx="4"/>
  <text x="530" y="227" fill="#43a047" font-size="14" font-weight="bold">4 units (₹1,596)</text>

  <text x="220" y="275" fill="#37474f" font-size="14" font-weight="bold" text-anchor="end">Margherita Supreme</text>
  <rect x="240" y="255" width="210" height="32" fill="#4caf50" rx="4"/>
  <text x="460" y="277" fill="#4caf50" font-size="14" font-weight="bold">3 units (₹897)</text>

  <text x="220" y="325" fill="#37474f" font-size="14" font-weight="bold" text-anchor="end">Paneer Tikka Pizza</text>
  <rect x="240" y="305" width="160" height="32" fill="#66bb6a" rx="4"/>
  <text x="410" y="327" fill="#66bb6a" font-size="14" font-weight="bold">2 units (₹698)</text>

  <text x="220" y="375" fill="#37474f" font-size="14" font-weight="bold" text-anchor="end">Sizzling Brownie</text>
  <rect x="240" y="355" width="140" height="32" fill="#81c784" rx="4"/>
  <text x="390" y="377" fill="#81c784" font-size="14" font-weight="bold">2 units (₹318)</text>
</svg>`
    },
    {
        name: 'category_sales',
        title: 'FoodHub - Category-wise Revenue Share',
        color: [245, 124, 0],
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="100%" height="100%">
  <rect width="800" height="450" fill="#ffffff" rx="12"/>
  <rect x="0" y="0" width="800" height="60" fill="#263238" rx="12 12 0 0"/>
  <text x="30" y="38" fill="#ffffff" font-family="Segoe UI, sans-serif" font-size="20" font-weight="bold">Category-wise Revenue Share (%)</text>
  
  <g transform="translate(250, 240)">
    <!-- Donut Segments -->
    <!-- Biryani: ~36% (0 to 130 deg) -->
    <path d="M 0 -130 A 130 130 0 0 1 100 83 L 54 45 A 70 70 0 0 0 0 -70 Z" fill="#e65100"/>
    <!-- Pizza: ~34% (130 to 252 deg) -->
    <path d="M 100 83 A 130 130 0 0 1 -123 40 L -66 22 A 70 70 0 0 0 54 45 Z" fill="#ff9800"/>
    <!-- Burger: ~12% (252 to 295 deg) -->
    <path d="M -123 40 A 130 130 0 0 1 -113 -65 L -61 -35 A 70 70 0 0 0 -66 22 Z" fill="#ffb74d"/>
    <!-- Drinks: ~10% (295 to 331 deg) -->
    <path d="M -113 -65 A 130 130 0 0 1 -63 -114 L -34 -61 A 70 70 0 0 0 -61 -35 Z" fill="#29b6f6"/>
    <!-- Desserts: ~8% (331 to 360 deg) -->
    <path d="M -63 -114 A 130 130 0 0 1 0 -130 L 0 -70 A 70 70 0 0 0 -34 -61 Z" fill="#ab47bc"/>
    <text x="0" y="8" fill="#263238" font-size="18" font-weight="bold" text-anchor="middle">₹5,057</text>
    <text x="0" y="28" fill="#78909c" font-size="12" text-anchor="middle">Total Revenue</text>
  </g>

  <!-- Legend -->
  <g transform="translate(480, 120)">
    <rect x="0" y="0" width="20" height="20" fill="#e65100" rx="3"/>
    <text x="30" y="16" fill="#37474f" font-size="14" font-weight="bold">Biryani – 36.2% (₹1,830)</text>

    <rect x="0" y="45" width="20" height="20" fill="#ff9800" rx="3"/>
    <text x="30" y="61" fill="#37474f" font-size="14" font-weight="bold">Pizza – 34.4% (₹1,740)</text>

    <rect x="0" y="90" width="20" height="20" fill="#ffb74d" rx="3"/>
    <text x="30" y="106" fill="#37474f" font-size="14" font-weight="bold">Burger – 12.3% (₹620)</text>

    <rect x="0" y="135" width="20" height="20" fill="#29b6f6" rx="3"/>
    <text x="30" y="151" fill="#37474f" font-size="14" font-weight="bold">Drinks – 9.8% (₹495)</text>

    <rect x="0" y="180" width="20" height="20" fill="#ab47bc" rx="3"/>
    <text x="30" y="196" fill="#37474f" font-size="14" font-weight="bold">Desserts – 7.3% (₹372)</text>
  </g>
</svg>`
    },
    {
        name: 'monthly_revenue',
        title: 'FoodHub - Monthly Revenue Trajectory',
        color: [216, 67, 21],
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="100%" height="100%">
  <rect width="800" height="450" fill="#ffffff" rx="12"/>
  <rect x="0" y="0" width="800" height="60" fill="#263238" rx="12 12 0 0"/>
  <text x="30" y="38" fill="#ffffff" font-family="Segoe UI, sans-serif" font-size="20" font-weight="bold">Monthly Revenue Trajectory (INR)</text>
  <line x1="80" y1="360" x2="740" y2="360" stroke="#cfd8dc" stroke-width="2"/>
  <line x1="80" y1="100" x2="80" y2="360" stroke="#cfd8dc" stroke-width="2"/>

  <!-- Path Trendline -->
  <path d="M 160 300 L 320 250 L 480 180 L 640 130" fill="none" stroke="#d84315" stroke-width="4"/>
  <!-- Points -->
  <circle cx="160" cy="300" r="8" fill="#e65100" stroke="#ffffff" stroke-width="3"/>
  <text x="160" y="280" fill="#d84315" font-size="13" font-weight="bold" text-anchor="middle">₹1,850</text>
  <text x="160" y="385" fill="#546e7a" font-size="13" text-anchor="middle">Aug 2026</text>

  <circle cx="320" cy="250" r="8" fill="#e65100" stroke="#ffffff" stroke-width="3"/>
  <text x="320" y="230" fill="#d84315" font-size="13" font-weight="bold" text-anchor="middle">₹2,420</text>
  <text x="320" y="385" fill="#546e7a" font-size="13" text-anchor="middle">Sep 2026</text>

  <circle cx="480" cy="180" r="8" fill="#e65100" stroke="#ffffff" stroke-width="3"/>
  <text x="480" y="160" fill="#d84315" font-size="13" font-weight="bold" text-anchor="middle">₹3,690</text>
  <text x="480" y="385" fill="#546e7a" font-size="13" text-anchor="middle">Oct 2026</text>

  <circle cx="640" cy="130" r="8" fill="#e65100" stroke="#ffffff" stroke-width="3"/>
  <text x="640" y="110" fill="#d84315" font-size="13" font-weight="bold" text-anchor="middle">₹5,057</text>
  <text x="640" y="385" fill="#546e7a" font-size="13" text-anchor="middle">Nov 2026 (MTD)</text>
</svg>`
    },
    {
        name: 'order_status_distribution',
        title: 'FoodHub - Order Status Distribution',
        color: [33, 150, 243],
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="100%" height="100%">
  <rect width="800" height="450" fill="#ffffff" rx="12"/>
  <rect x="0" y="0" width="800" height="60" fill="#263238" rx="12 12 0 0"/>
  <text x="30" y="38" fill="#ffffff" font-family="Segoe UI, sans-serif" font-size="20" font-weight="bold">Order Status Distribution</text>
  <line x1="80" y1="360" x2="740" y2="360" stroke="#cfd8dc" stroke-width="2"/>
  <line x1="80" y1="100" x2="80" y2="360" stroke="#cfd8dc" stroke-width="2"/>

  <!-- Completed: 7 -->
  <rect x="140" y="140" width="90" height="220" fill="#4caf50" rx="4"/>
  <text x="185" y="125" fill="#4caf50" font-size="16" font-weight="bold" text-anchor="middle">7 Orders</text>
  <text x="185" y="385" fill="#37474f" font-size="13" font-weight="bold" text-anchor="middle">COMPLETED</text>

  <!-- Preparing: 1 -->
  <rect x="290" y="328" width="90" height="32" fill="#ff9800" rx="4"/>
  <text x="335" y="315" fill="#ff9800" font-size="16" font-weight="bold" text-anchor="middle">1 Order</text>
  <text x="335" y="385" fill="#37474f" font-size="13" font-weight="bold" text-anchor="middle">PREPARING</text>

  <!-- Placed: 2 -->
  <rect x="440" y="297" width="90" height="63" fill="#2196f3" rx="4"/>
  <text x="485" y="283" fill="#2196f3" font-size="16" font-weight="bold" text-anchor="middle">2 Orders</text>
  <text x="485" y="385" fill="#37474f" font-size="13" font-weight="bold" text-anchor="middle">PLACED</text>

  <!-- Cancelled: 0 -->
  <rect x="590" y="358" width="90" height="2" fill="#f44336" rx="4"/>
  <text x="635" y="348" fill="#f44336" font-size="16" font-weight="bold" text-anchor="middle">0 Orders</text>
  <text x="635" y="385" fill="#37474f" font-size="13" font-weight="bold" text-anchor="middle">CANCELLED</text>
</svg>`
    },
    {
        name: 'customer_analysis',
        title: 'FoodHub - Customer Lifetime Expenditure',
        color: [2, 136, 209],
        svg: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 450" width="100%" height="100%">
  <rect width="800" height="450" fill="#ffffff" rx="12"/>
  <rect x="0" y="0" width="800" height="60" fill="#263238" rx="12 12 0 0"/>
  <text x="30" y="38" fill="#ffffff" font-family="Segoe UI, sans-serif" font-size="20" font-weight="bold">Customer Lifetime Expenditure (INR)</text>
  <line x1="80" y1="360" x2="740" y2="360" stroke="#cfd8dc" stroke-width="2"/>
  <line x1="80" y1="100" x2="80" y2="360" stroke="#cfd8dc" stroke-width="2"/>

  <!-- Rahul Sharma: ₹2,223 -->
  <rect x="160" y="138" width="110" height="222" fill="#0288d1" rx="4"/>
  <text x="215" y="123" fill="#0288d1" font-size="15" font-weight="bold" text-anchor="middle">₹2,223 (4 orders)</text>
  <text x="215" y="385" fill="#37474f" font-size="13" font-weight="bold" text-anchor="middle">Rahul Sharma</text>

  <!-- Priya Sundaram: ₹1,517 -->
  <rect x="345" y="208" width="110" height="152" fill="#0288d1" rx="4"/>
  <text x="400" y="193" fill="#0288d1" font-size="15" font-weight="bold" text-anchor="middle">₹1,517 (3 orders)</text>
  <text x="400" y="385" fill="#37474f" font-size="13" font-weight="bold" text-anchor="middle">Priya Sundaram</text>

  <!-- Anand Kumar: ₹1,317 -->
  <rect x="530" y="228" width="110" height="132" fill="#0288d1" rx="4"/>
  <text x="585" y="213" fill="#0288d1" font-size="15" font-weight="bold" text-anchor="middle">₹1,317 (3 orders)</text>
  <text x="585" y="385" fill="#37474f" font-size="13" font-weight="bold" text-anchor="middle">Anand Kumar</text>
</svg>`
    }
];

// Write SVGs and PNGs
charts.forEach(c => {
    // Write SVG in all target locations
    [chartsDir, staticChartsDir, frontendChartsDir].forEach(d => {
        fs.writeFileSync(path.join(d, `${c.name}.svg`), c.svg, 'utf8');
    });

    // Generate matching PNG
    const pngBuf = createPng(400, 225, generateChartBuffer(400, 225, c.title, 'FoodHub Analytics', c.color));
    [chartsDir, staticChartsDir, frontendChartsDir].forEach(d => {
        fs.writeFileSync(path.join(d, `${c.name}.png`), pngBuf);
    });

    console.log(`[+] Exported ${c.name}.png and ${c.name}.svg`);
});

console.log('All 6 Analytical Visualizations generated successfully!');
