# AgriScan — Smart Crop Health Analyzer

> **Healthy Crops. Better Yields. A Brighter Future.**

AgriScan is an evidence-based, farmer-first mobile application built with modern native Android (Kotlin & Jetpack Compose), AndroidX CameraX, Firebase AI Logic (Gemini 3.8 Flash), and an offline-first Room SQLite persistence layer.

---

## 🌟 Key Capabilities & Features

### 1. Real-Time CameraX & Crop Scanner
- **Live Viewfinder**: Real-time camera preview with an agricultural scanning frame and targeting reticle.
- **Hardware Controls**: Flash/torch toggle, front/rear camera switcher, linear zoom control.
- **Multimodal Inputs**: Live camera capture, zero-permission system photo picker, and one-tap **Demo Sample Leaf** inspection to ensure instant evaluation in emulator environments.
- **Target Crop Selector**: Quick-select major field and horticultural crops (Tomato, Paddy/Rice, Chilli, Cotton, Wheat, Potato, Maize).

### 2. Evidence-Based AI Diagnosis & Localization
- **Multimodal AI**: Powered by **Gemini 3.8 Flash** with structured JSON output schema (`Schema.obj`).
- **Symptom Localization**: Normalized bounding box coordinates overlayed directly onto the crop photograph with targeting corners and crosshairs.
- **Calibrated Health Meter**: Dynamic animated score (0–100%) with transparent disclosure of contributing factors (lesion density, chlorotic spread, organ impact).
- **Differential Pathology**: Distinguishes between visual symptoms, probable causes, microclimate triggers (e.g. leaf wetness, humidity), and similar-looking diseases (e.g. Early Blight vs Late Blight vs Septoria).

### 3. Integrated Pest Management (IPM) & Safe Treatments
- **Non-Chemical & Cultural Priority**: Pruning bottom foliage, spacing, straw mulching, bio-fungicides (*Trichoderma viride*, *Bacillus subtilis*), and cold-pressed neem formulations.
- **Registered Chemical Controls**: Precise label dilutions (e.g. Mancozeb 75% WP @ 2.0g/L, Copper Oxychloride 50% WP @ 2.5g/L).
- **Mandatory Safety (PPE)**: Operator protection, Re-Entry Intervals (REI), Pre-Harvest Intervals (PHI), and pollinator cautions.

### 4. Official Extension Grounding & Verifiable Citations
- All recommendations cite verified agricultural research bodies:
  - **ICAR** (Indian Council of Agricultural Research, IIHR, NRRI)
  - **FAO** (Food and Agriculture Organization Plant Protection Division)
  - **State Agricultural Universities & KVKs** (TNAU, PAU, UAS Bangalore)
- Tappable direct links to official guidance and extension manuals.

### 5. AgriScan AI Conversational Doctor
- Multi-turn agricultural chat with streaming response animation.
- Context-aware: Automatically references the active crop scan, health score, and detected symptoms.
- Suggested prompt chips for instant farmer questions.
- Built-in Text-to-Speech (TTS) audio playback for accessibility in the field.

### 6. Persistent History & Scan Progression Comparison
- **Room SQLite Local Database**: Scans, farm records, reminders, and chat history persist across device reboots and app launches.
- **Filtering & Search**: Search by crop name, scientific pathogen, or filter by category (Healthy, Disease Suspected, Pest Detected, Follow-up Needed).
- **Comparison Mode**: Compare two scans side-by-side to track disease progression or evaluate treatment recovery (+% health delta).

### 7. Farm & Plot Management
- Register multiple farms with acreage, soil type (Red Sandy Loam, Clay, Alluvial), and irrigation infrastructure (Drip, Sprinkler, Flood).
- Subdivide into field plots with crop variety, planting date, and growth stage.

### 8. Crop Care Reminders
- Schedule post-spray rechecks, alternate wetting and drying (AWD) water checks, and harvesting milestones with completion toggles.

### 9. Multilingual & Farmer Accessibility
- Extensible localization ready for:
  - English
  - हिन्दी (Hindi)
  - ಕನ್ನಡ (Kannada)
  - తెలుగు (Telugu)
  - தமிழ் (Tamil)
  - मराठी (Marathi)
  - বাংলা (Bengali)
- Large 48dp+ interactive touch targets, high-contrast agricultural color tokens, and simplified terminology.

---

## 🏗️ Architecture & Technology Stack

```
com.example
├── MainActivity.kt               // Single-Activity, App Check wiring, Navigation
├── util/
│   └── AppCheckConfig.kt         // Firebase App Check debug attestation helper
├── data/
│   ├── model/                    // CropScan, Farm, Field, ChatMessage, TaskReminder, UserProfile
│   ├── local/                    // Room Database, Converters, ScanDao, FarmDao, ReminderDao, ChatDao
│   ├── ai/                       // AgriScanAiService (Gemini 3.8 Flash + Resilient Pathology Engine)
│   └── repository/               // AgriRepository (SSOT coordinating DB, AI, and Telemetry)
└── ui/
    ├── navigation/               // Screen routes
    ├── theme/                    // AgriScan forest green & agricultural palette
    ├── components/               // HealthScoreIndicator, ScanBoundingBoxOverlay, AgriBottomBar
    ├── viewmodel/                // AgriViewModel (StateFlows for scans, chat, filters, analysis)
    └── screens/                  // Welcome, Auth, Home, CameraScan, AnalysisResult,
                                  // DiagnosisDetail, TreatmentPlan, AiChat, History,
                                  // Comparison, Farm, Reminders, Settings
```

---

## 🔒 Security & Privacy

1. **Zero-Trust Storage**: Farm boundary maps, yields, and crop photos remain private to the device.
2. **App Check Attestation**: Firebase App Check debug token is registered in `MainActivity.onCreate()` using synchronous `.commit()` storage to protect backend resources.
3. **No Hardcoded Keys**: Client code does not contain hardcoded credentials. All configuration is safely managed via AI Studio environment settings.

---

## 🧪 Testing

Automated tests in `app/src/test/java/com/example/`:
- `AgriScanRobolectricTest`: Verifies Room DB insertions, DAO operations, diagnosis engine logic, and citation integrity without requiring a physical device.
- Run tests via terminal:
  ```bash
  gradle :app:testDebugUnitTest
  ```
