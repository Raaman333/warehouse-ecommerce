package com.example.warehouse.config;

import com.example.warehouse.model.ProductDetails;
import com.example.warehouse.repository.ProductDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Automatically seeds the MongoDB product_details collection with
 * rich metadata, HD photography, brands, specifications, and descriptions
 * for the 35 products in the Indian catalog.
 */
@Component
@Order(2)
public class MongoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoDataInitializer.class);
    private final ProductDetailsRepository mongoRepo;

    public MongoDataInitializer(ProductDetailsRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public void run(String... args) {
        seedCatalog();
    }

    public synchronized int seedCatalog() {
        try {
            log.info("Populating MongoDB product_details for 35 Indian catalog items...");

            List<ProductDetails> items = List.of(
                // ─── Laptops & Computers ───
                create("PROD-001", "UltraTech",
                    "The UltraBook Pro 15 delivers uncompromising performance with an Intel Core Ultra 9, 32GB LPDDR5X RAM, and a breathtaking 15.6\" 2.8K OLED 120Hz display.",
                    "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600",
                    Map.of("Processor", "Intel Core Ultra 9-185H", "RAM", "32GB LPDDR5X", "Storage", "1TB NVMe SSD", "Display", "15.6\" 2.8K OLED", "Battery", "86Wh, 20hr", "Weight", "1.4 kg"),
                    List.of("laptop", "ultrabook", "intel", "oled", "professional")),

                create("PROD-011", "ASUS",
                    "ROG Zephyrus G16 is an ultra-thin gaming marvel equipped with AMD Ryzen 9 8945HS and NVIDIA GeForce RTX 4070 8GB GDDR6 with 2.5K 240Hz OLED ROG Nebula display.",
                    "https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=600",
                    Map.of("Processor", "AMD Ryzen 9 8945HS", "Graphics", "NVIDIA RTX 4070 8GB", "RAM", "32GB LPDDR5X", "Storage", "1TB Gen4 SSD", "Display", "16\" 2.5K 240Hz OLED", "Weight", "1.85 kg"),
                    List.of("gaming", "laptop", "asus", "rog", "rtx4070")),

                create("PROD-012", "Apple",
                    "Supercharged by the M3 chip, MacBook Air 15-inch provides blazing-fast processing in an impossibly thin aluminum enclosure with up to 18 hours of battery life.",
                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600",
                    Map.of("Chip", "Apple M3 (8-core CPU, 10-core GPU)", "Memory", "16GB Unified", "Storage", "512GB SSD", "Display", "15.3\" Liquid Retina", "Battery", "Up to 18 hours", "Weight", "1.51 kg"),
                    List.of("apple", "macbook", "m3", "laptop", "retina")),

                create("PROD-013", "Lenovo",
                    "Engineered for business leaders, ThinkPad X1 Carbon Gen 12 blends military-grade durability with Intel Core Ultra 7 and Intel Arc graphics in a featherlight carbon-fiber chassis.",
                    "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600",
                    Map.of("Processor", "Intel Core Ultra 7 155H", "RAM", "16GB LPDDR5X", "Storage", "512GB PCIe 4.0 SSD", "Security", "Fingerprint + IR Face ID", "Weight", "1.09 kg"),
                    List.of("lenovo", "thinkpad", "business", "carbon", "ultrabook")),

                // ─── Mobiles & Tablets ───
                create("PROD-014", "Apple",
                    "iPhone 16 Pro Max features a strong and light Grade 5 titanium design with a larger 6.9-inch Super Retina XDR display, Camera Control, 4K 120 fps Dolby Vision, and the A18 Pro chip.",
                    "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=600",
                    Map.of("Chip", "A18 Pro Bionic", "Display", "6.9\" Super Retina XDR OLED", "Camera", "48MP Fusion + 48MP Ultra Wide + 5x Telephoto", "Build", "Grade 5 Titanium", "Battery", "Up to 33 hours video"),
                    List.of("iphone", "apple", "pro-max", "smartphone", "5g")),

                create("PROD-015", "Samsung",
                    "Galaxy S24 Ultra ushers in the era of Galaxy AI with a 200MP camera, built-in S Pen, titanium exterior, and Snapdragon 8 Gen 3 for Galaxy.",
                    "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=600",
                    Map.of("Processor", "Snapdragon 8 Gen 3 for Galaxy", "Display", "6.8\" Dynamic AMOLED 2X 120Hz", "Camera", "200MP + 50MP + 12MP + 10MP", "Stylus", "Integrated S-Pen", "Battery", "5000 mAh"),
                    List.of("samsung", "galaxy", "s24-ultra", "ai", "5g")),

                create("PROD-016", "OnePlus",
                    "OnePlus 12 5G represents flagship luxury with 4th Gen Hasselblad Camera for Mobile, 2K 120Hz ProXDR display, Snapdragon 8 Gen 3, and 100W SUPERVOOC ultra-fast charging.",
                    "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600",
                    Map.of("Processor", "Snapdragon 8 Gen 3", "Charging", "100W Wired + 50W Wireless AIRVOOC", "Display", "6.82\" 2K 120Hz LTPO AMOLED", "Battery", "5400 mAh Dual-cell"),
                    List.of("oneplus", "oneplus12", "hasselblad", "fast-charge", "5g")),

                create("PROD-017", "Apple",
                    "iPad Air 11-inch with M2 chip delivers phenomenal performance, Liquid Retina display, Touch ID, landscape front camera, and compatibility with Apple Pencil Pro and Magic Keyboard.",
                    "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600",
                    Map.of("Chip", "Apple M2 (8-core CPU, 9-core GPU)", "Display", "11\" Liquid Retina True Tone", "Storage", "128GB", "Cameras", "12MP Wide back, 12MP Center Stage landscape"),
                    List.of("ipad", "apple", "m2", "tablet", "apple-pencil")),

                // ─── Audio & Sound ───
                create("PROD-002", "Sony",
                    "Sony WH-1000XM5 wireless noise-canceling headphones with two processors and 8 microphones for unprecedented noise cancellation and exceptional hands-free call quality.",
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600",
                    Map.of("ANC", "Dual processor 8-microphone ANC", "Battery", "30 hours (3min charge = 3hr playback)", "Audio", "Hi-Res LDAC & DSEE Extreme", "Weight", "250g"),
                    List.of("sony", "headphones", "anc", "audio", "bluetooth")),

                create("PROD-018", "Apple",
                    "AirPods Pro (2nd generation) with USB-C case delivers up to 2x more Active Noise Cancellation, Adaptive Audio, Transparency mode, and Personalized Spatial Audio with dynamic head tracking.",
                    "https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=600",
                    Map.of("Chip", "Apple H2 headphone chip", "Noise Control", "2x Active Noise Cancellation + Adaptive Audio", "Charging", "MagSafe USB-C + Apple Watch charger", "Water Resistance", "IP54"),
                    List.of("airpods", "apple", "earbuds", "anc", "spatial-audio")),

                create("PROD-019", "Marshall",
                    "Marshall Stanmore III occupies the sonic middleweight space of the home lineup, delivering expansive Marshall signature sound with re-engineered wider soundstage and vintage brass accents.",
                    "https://images.unsplash.com/photo-1545454675-3531b543be5d?w=600",
                    Map.of("Amplifiers", "1× 50W Class D (woofer) + 2× 15W Class D (tweeters)", "Connectivity", "Bluetooth 5.2, 3.5mm AUX, RCA", "Frequency", "45–20,000 Hz", "Finish", "Iconic Black Vinyl & Brass"),
                    List.of("marshall", "speaker", "bluetooth", "vintage", "audio")),

                create("PROD-020", "JBL",
                    "JBL Cinema SB271 features 220W system power, Dolby Digital, HDMI ARC, and a wireless subwoofer for deep bass and immersive home theater clarity.",
                    "https://images.unsplash.com/photo-1543512214-318c7553f230?w=600",
                    Map.of("Power Output", "220W Max", "Subwoofer", "Wireless 5.25\" Subwoofer", "Sound", "Dolby Digital 2.1 embedded", "Inputs", "HDMI ARC, Optical, Bluetooth"),
                    List.of("jbl", "soundbar", "home-theater", "dolby", "subwoofer")),

                // ─── Televisions ───
                create("PROD-003", "Sony",
                    "Sony Bravia XR 55-inch 4K OLED TV powered by Cognitive Processor XR reproduces pure OLED blacks and billion accurate colors with Acoustic Surface Audio+ sound directly from the screen.",
                    "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=600",
                    Map.of("Panel", "4K OLED (3840×2160) 120Hz", "Processor", "Cognitive Processor XR", "HDR", "Dolby Vision, HDR10, HLG", "Gaming", "4K 120fps, VRR, ALLM, HDMI 2.1", "OS", "Google TV"),
                    List.of("sony", "bravia", "oled", "tv", "4k")),

                create("PROD-021", "LG",
                    "LG OLED evo C3 65-inch delivers self-lit pixels with Brightness Booster, α9 AI Processor Gen6, 0.1ms response time, NVIDIA G-Sync, AMD FreeSync Premium, and Dolby Vision.",
                    "https://images.unsplash.com/photo-1461151304267-38535e780c79?w=600",
                    Map.of("Panel", "65\" OLED evo 4K 120Hz", "Processor", "α9 AI Processor 4K Gen6", "Gaming", "4× HDMI 2.1, 0.1ms, G-Sync, FreeSync", "Audio", "Dolby Atmos 40W 2.2ch"),
                    List.of("lg", "oled", "smart-tv", "gaming-tv", "dolby-atmos")),

                create("PROD-022", "Samsung",
                    "Samsung 43-inch Crystal 4K Vivid Pro Smart TV with PurColor, 4K Upscaling, SolarCell Remote control, and seamless smart home control via SmartThings.",
                    "https://images.unsplash.com/photo-1552975084-6e027cd345c2?w=600",
                    Map.of("Resolution", "4K Ultra HD (3840×2160)", "Engine", "Crystal Processor 4K", "Audio", "Q-Symphony 20W", "Remote", "SolarCell Battery-free Remote"),
                    List.of("samsung", "crystal-4k", "smart-tv", "budget-tv", "4k")),

                // ─── Smart Wearables ───
                create("PROD-023", "Apple",
                    "Apple Watch Series 10 is the thinnest Apple Watch ever, featuring the biggest display, sleep apnea notifications, faster charging, and depth and water temperature sensors.",
                    "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=600",
                    Map.of("Display", "Wide-angle OLED Always-On Retina", "Sensors", "ECG, Blood Oxygen, Sleep Apnea, Depth & Temp", "Charging", "80% in 30 minutes", "Water Resistance", "50m swimproof"),
                    List.of("apple-watch", "smartwatch", "fitness", "health", "apple")),

                create("PROD-024", "Samsung",
                    "Galaxy Watch Ultra features a military-grade titanium cushion design, dual-frequency GPS, personalized Heart Rate Zones, and up to 100 hours in Power Saving mode.",
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                    Map.of("Material", "Grade 4 Titanium + Sapphire Crystal", "Durability", "10ATM + IP68 + MIL-STD-810H", "Battery", "Up to 100 hours", "GPS", "Dual-Frequency L1+L5"),
                    List.of("samsung", "galaxy-watch", "ultra", "titanium", "outdoor")),

                create("PROD-025", "Garmin",
                    "Garmin Forerunner 265 GPS running smartwatch lights up your training with a brilliant AMOLED touchscreen, morning report, training readiness score, and multi-band GPS.",
                    "https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=600",
                    Map.of("Display", "1.3\" AMOLED Touchscreen", "Battery Life", "Up to 13 days in smartwatch mode", "Metrics", "HRV Status, Training Readiness, VO2 Max", "Weight", "47g"),
                    List.of("garmin", "running", "gps", "marathon", "fitness")),

                // ─── Cameras & Photography ───
                create("PROD-026", "Sony",
                    "Sony Alpha 7 IV redefines full-frame hybrid shooting with a 33MP Exmor R CMOS sensor, 4K 60p 10-bit recording, AI-based Real-time Eye AF, and 5-axis in-body image stabilization.",
                    "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600",
                    Map.of("Sensor", "33MP Full-Frame Exmor R CMOS", "Video", "4K 60p 10-bit 4:2:2 All-Intra", "Autofocus", "759 phase-detection points Eye AF", "Stabilization", "5.5-step 5-axis IBIS"),
                    List.of("sony", "alpha", "mirrorless", "camera", "4k-video")),

                create("PROD-027", "DJI",
                    "DJI Osmo Pocket 3 Creator Combo boasts a powerful 1-inch CMOS sensor, 4K 120fps video, 3-axis mechanical stabilization, 2-inch rotatable OLED screen, and wireless microphone transmitter.",
                    "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=600",
                    Map.of("Sensor", "1-inch CMOS Sensor", "Video", "4K UHD at 120fps slow-motion", "Stabilization", "3-axis mechanical motorized gimbal", "Microphone", "DJI Mic 2 Transmitter Included"),
                    List.of("dji", "osmo-pocket", "vlogging", "gimbal", "creator")),

                create("PROD-028", "GoPro",
                    "GoPro HERO13 Black action camera captures jaw-dropping 5.3K 60fps video with award-winning HyperSmooth 6.0 stabilization, HDR video, rugged waterproof body up to 10m, and magnetic latch mounting.",
                    "https://images.unsplash.com/photo-1565849904461-04a58ad377e0?w=600",
                    Map.of("Resolution", "5.3K 60fps / 4K 120fps / 2.7K 240fps", "Stabilization", "HyperSmooth 6.0 + 360° Horizon Lock", "Waterproof", "10m (33ft) without housing", "Battery", "1900mAh Enduro Battery"),
                    List.of("gopro", "action-cam", "waterproof", "hypersmooth", "adventure")),

                // ─── Gaming & Peripherals ───
                create("PROD-029", "Sony",
                    "PlayStation 5 Slim Disc Edition unleashes new gaming possibilities with ultra-high speed 1TB SSD, ray tracing, 4K 120Hz output, and the immersive DualSense wireless controller with haptic feedback.",
                    "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=600",
                    Map.of("CPU", "8-core AMD Zen 2", "GPU", "10.3 TFLOPS AMD RDNA 2", "Storage", "1TB Custom NVMe SSD", "Output", "4K 120Hz, HDR, 8K ready", "Controller", "DualSense Haptic & Adaptive Triggers"),
                    List.of("ps5", "playstation", "sony", "gaming-console", "4k-gaming")),

                create("PROD-030", "Nintendo",
                    "Nintendo Switch OLED Model brings vibrant 7-inch OLED screen gaming on the go with a wide adjustable stand, wired LAN dock, 64GB storage, and enhanced audio.",
                    "https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=600",
                    Map.of("Screen", "7-inch OLED Touchscreen", "Modes", "TV Mode, Tabletop Mode, Handheld Mode", "Storage", "64GB Internal + microSD expansion", "Battery", "4.5 to 9 hours"),
                    List.of("nintendo", "switch", "oled", "handheld", "gaming")),

                create("PROD-004", "KeyForge",
                    "TKL 87-key mechanical keyboard with tactile Cherry MX Brown switches, solid CNC aluminum aircraft-grade faceplate, per-key RGB backlighting, and detachable braided USB-C cable.",
                    "https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=600",
                    Map.of("Switch Type", "Cherry MX Brown Tactile", "Layout", "Tenkeyless (87 Keys)", "Chassis", "CNC Anodized Aluminium", "Polling Rate", "1000Hz (1ms)"),
                    List.of("keyboard", "mechanical", "rgb", "cherry-mx", "peripherals")),

                create("PROD-031", "Logitech",
                    "Logitech G502 X PLUS LIGHTSPEED wireless gaming mouse pairs optical-mechanical LIGHTFORCE hybrid switches with the HERO 25K sub-micron sensor and dynamic LIGHTSYNC 8-zone RGB.",
                    "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600",
                    Map.of("Sensor", "HERO 25K (100–25,600 DPI)", "Switches", "LIGHTFORCE Optical-Mechanical", "Battery", "Up to 120 hours continuous gaming", "Buttons", "13 Programmable Controls"),
                    List.of("logitech", "mouse", "wireless", "gaming-mouse", "rgb")),

                create("PROD-009", "Logitech",
                    "Logitech Brio 4K Pro Ultra HD webcam offers crystal-clear 4K streaming with RightLight 3 HDR, dual omnidirectional noise-canceling microphones, and Windows Hello facial recognition login.",
                    "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?w=600",
                    Map.of("Resolution", "4K UHD @ 30fps / 1080p @ 60fps", "Field of View", "Adjustable 65°, 78°, and 90°", "Security", "Infrared sensor Windows Hello certified", "Connection", "USB 3.0 Type-C"),
                    List.of("webcam", "logitech", "4k", "streaming", "video-conference")),

                // ─── Home & Kitchen Appliances ───
                create("PROD-032", "Dyson",
                    "Dyson V12 Detect Slim Total Clean is Dyson's lightest intelligent cordless vacuum with laser illumination that reveals invisible microscopic dust on hard floors and an LCD screen showing particle counts.",
                    "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=600",
                    Map.of("Motor", "Dyson Hyperdymium 125,000rpm", "Suction Power", "150 AW", "Filtration", "Whole-machine filtration captures 99.99% particles down to 0.3 microns", "Weight", "2.2 kg"),
                    List.of("dyson", "vacuum", "cordless", "home-appliances", "cleaning")),

                create("PROD-033", "Philips",
                    "Philips Digital Air Fryer HD9252 with patented Rapid Air technology circulates hot air to cook crispy, delicious food with up to 90% less oil. Includes 7 touch presets and Keep Warm mode.",
                    "https://images.unsplash.com/photo-1584992236310-6edddc08acff?w=600",
                    Map.of("Capacity", "4.1 Litres (0.8 kg food)", "Power", "1400 Watts", "Presets", "Frozen snacks, fresh fries, meat, fish, cake, grilled veggies", "Cleaning", "QuickClean non-stick basket dishwasher safe"),
                    List.of("philips", "air-fryer", "kitchen", "healthy-cooking", "appliances")),

                create("PROD-034", "Coway",
                    "Coway Airmega 150 air purifier features Green Anti-Virus True HEPA filtration that traps 99.999% of airborne particles down to 0.01 microns, real-time pollution sensor ring, and whisper-quiet operation.",
                    "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600",
                    Map.of("Coverage", "Up to 355 sq ft (ideal for bedrooms & offices)", "Filter", "Pre-Filter + Deodorization + Green True HEPA", "Noise Level", "22 dB silent mode", "Certification", "ECARF, Clean Air Delivery Rate certified"),
                    List.of("coway", "air-purifier", "hepa", "smart-home", "clean-air")),

                create("PROD-035", "Ecovacs",
                    "Ecovacs Deebot N10 robotic vacuum and mop utilizes dToF laser mapping (TrueMapping 2.0) to navigate complex homes with 4300Pa suction power and OZMO Mopping System.",
                    "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600",
                    Map.of("Suction", "4300 Pa Powerful Suction", "Navigation", "TrueMapping 2.0 dToF LiDAR", "Mopping", "OZMO Electronically controlled water pump", "Runtime", "300 minutes on 5200mAh battery"),
                    List.of("ecovacs", "robot-vacuum", "mop", "smart-home", "automation")),

                // ─── Accessories, Storage & Networking ───
                create("PROD-005", "ConnectPro",
                    "14-in-1 Triple Display USB-C Docking Station with 140W Power Delivery pass-through, triple 4K 60Hz outputs, 2.5 Gigabit Ethernet, and 7 high-speed USB ports.",
                    "https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=600",
                    Map.of("Power Delivery", "140W USB-C PD Charging", "Video Out", "2× DisplayPort 1.4 + 1× HDMI 2.0 (Triple 4K)", "Networking", "2.5G Multi-Gigabit Ethernet", "Compatibility", "Windows, macOS, Linux"),
                    List.of("docking-station", "usb-c", "workstation", "accessories", "productivity")),

                create("PROD-006", "SanDisk",
                    "SanDisk Extreme Portable SSD 2TB delivers NVMe solid-state performance with 2000MB/s read/write speeds, 3-meter drop protection, IP65 water and dust resistance, and 256-bit AES hardware encryption.",
                    "https://images.unsplash.com/photo-1601737487795-dab272f52420?w=600",
                    Map.of("Capacity", "2TB NVMe SSD", "Speed", "Up to 2,000 MB/s Read & Write", "Durability", "IP65 Water/Dust + 3m Drop resistant", "Interface", "USB 3.2 Gen 2×2 Type-C"),
                    List.of("sandisk", "ssd", "portable-storage", "backup", "nvme")),

                create("PROD-007", "NexusHome",
                    "NexusHome Universal Smart Hub connects Zigbee 3.0, Z-Wave Plus, Thread, and Matter devices into a unified local smart home system with offline automation and HomeKit/Alexa/Google compatibility.",
                    "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600",
                    Map.of("Protocols", "Zigbee 3.0, Z-Wave Plus 800, Thread, Matter", "Processing", "Local offline automation engine", "Assistant", "Apple HomeKit, Amazon Alexa, Google Assistant", "Networking", "Wi-Fi 6 + Gigabit Ethernet PoE"),
                    List.of("smart-hub", "matter", "zigbee", "home-automation", "smart-home")),

                create("PROD-008", "Featherlite",
                    "Featherlite Ergonomic High-Back Mesh Chair features dynamic lumbar support, breathable Korean mesh, 4D adjustable armrests, and synchro-tilt mechanism certified by BIFMA for 10-year durability.",
                    "https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?w=600",
                    Map.of("Material", "Breathable Polymer Korean Mesh", "Ergonomics", "4D Armrests, 3-position synchro tilt, Lumbar depth", "Gas Lift", "Class 4 Heavy Duty (Up to 150 kg)", "Warranty", "10 Years Official Warranty"),
                    List.of("featherlite", "office-chair", "ergonomic", "furniture", "work-from-home")),

                create("PROD-010", "TP-Link",
                    "TP-Link Archer AXE75 Wi-Fi 6E Tri-Band Router unlocks the pristine 6GHz band delivering up to 5.4 Gbps speeds for buffer-free 8K streaming, VR gaming, and 200+ connected devices.",
                    "https://images.unsplash.com/photo-1606904825846-647eb07f5be2?w=600",
                    Map.of("Standards", "Wi-Fi 6E (6GHz + 5GHz + 2.4GHz)", "Speed", "2402 Mbps (6GHz) + 2402 Mbps (5GHz) + 574 Mbps (2.4GHz)", "Antennas", "6× High-Gain Antennas with Beamforming", "Security", "TP-Link HomeShield + WPA3"),
                    List.of("router", "wifi6e", "tp-link", "networking", "tri-band"))
            );

            mongoRepo.saveAll(items);
            log.info("MongoDB successfully seeded with all {} product details!", items.size());
            return items.size();
        } catch (Exception e) {
            log.warn("Could not seed MongoDB catalog (non-fatal): {}", e.getMessage());
            return 0;
        }
    }

    private ProductDetails create(String prodId, String brand, String desc, String img,
                                  Map<String, String> specs, List<String> tags) {
        ProductDetails p = new ProductDetails();
        p.setProdId(prodId);
        p.setBrand(brand);
        p.setDescription(desc);
        p.setImageUrl(img);
        p.setSpecifications(specs);
        p.setTags(tags);
        return p;
    }
}
