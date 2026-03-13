# InVoice Maker: A Cross-Platform GST Invoice Generation and Business Management System

## (FINAL YEAR PROJECT)

---

&nbsp;

**Session:**&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2022 – 2026

**Program:**&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Bachelor of Science in Software Engineering

---

**Submitted By:**

| Name | Roll No. |
|------|----------|
| Muhammad Junaid | SUIT-22-01-001-XXXX |
| _(Team Member 2)_ | SUIT-22-01-001-XXXX |
| _(Team Member 3)_ | SUIT-22-01-001-XXXX |

**Supervised By:** _(Supervisor Name)_

---

**SARHAD UNIVERSITY OF SCIENCE & INFORMATION TECHNOLOGY PESHAWAR**

---

&nbsp;

## 1. Abstract

This project presents **InVoice Maker**, an intelligent cross-platform mobile application for generating GST-compliant invoices and managing business operations. The system enables small businesses, freelancers, and traders to create professional invoices within seconds, track customers and inventory items, monitor sales analytics, and export invoice records — all without requiring an internet connection. The application is built using modern cross-platform technologies including Kotlin Multiplatform (KMP), Jetpack Compose Multiplatform, Room Database, and MVVM architecture with Koin dependency injection. The proposed solution aims to simplify the invoice generation process for micro and small businesses by delivering a fast, reliable, offline-first, and legally compliant billing experience on both Android and iOS platforms.

---

## 2. Introduction

With the rapid adoption of digital tools in business operations, small and medium enterprises (SMEs) and freelancers are increasingly looking for affordable, easy-to-use tools to handle their billing and invoicing requirements. In many developing economies, especially post-GST (Goods and Services Tax) implementation, businesses are legally required to issue tax-compliant invoices for every transaction. However, most existing invoice tools are either too complex, web-dependent, or expensive for small business owners.

**InVoice Maker** is designed to solve this problem by providing a lightweight, offline-first mobile application that allows users to generate GST-compliant invoices in under 30 seconds. The application integrates real-time sales analytics, customer management, product/item catalogue, multi-language support, and a professional invoice preview and sharing mechanism — all within a single, intuitive platform. By leveraging the power of Kotlin Multiplatform, the application targets both Android and iOS from a single shared codebase, significantly reducing development cost and maintenance overhead.

---

## 3. Problem Statement

Despite the availability of various billing and invoicing tools, small businesses and independent professionals face several critical challenges:

- **Lack of offline-first solutions:** Most invoice tools require active internet connectivity, making them unreliable for users in areas with poor network coverage.
- **Complex GST compliance:** Generating invoices with correct CGST, SGST, IGST breakdown, and GSTIN fields is challenging without a specialized tool.
- **No integrated business management:** Most tools focus solely on invoice generation without providing customer management, item catalogues, or sales dashboards.
- **Platform dependency:** Existing solutions are often restricted to a single platform (Android or web), excluding iOS users.
- **High cost of professional tools:** Enterprise billing software is priced beyond the reach of micro and small businesses.
- **Limited multi-language support:** Most tools are available only in English, excluding non-English-speaking business owners.

These challenges highlight the need for a dedicated, offline-capable, cross-platform invoice generation and business management application that is affordable, GST-compliant, and accessible to a wide range of users.

---

## 4. Objectives of the Project

The main objective of this project is to develop a modern, cross-platform, and offline-first mobile application that simplifies GST invoice generation and business management for small businesses and freelancers.

### 4.1 Specific Objectives

1. To develop a modern cross-platform UI using Jetpack Compose Multiplatform targeting Android and iOS.
2. To implement GST-compliant invoice generation with automatic CGST, SGST, and IGST calculation.
3. To build a customer management module for storing and retrieving client information.
4. To develop an item/product catalogue with pricing and tax rate configuration.
5. To provide a real-time sales analytics dashboard showing total sales, paid and unpaid invoice counts.
6. To implement a professional invoice preview with PDF export and sharing capability.
7. To support multi-language and multi-currency settings for global usability.
8. To store all data locally using Room Database, ensuring full offline functionality.
9. To implement a freemium subscription model (Pro upgrade via Paywall screen) to support monetization.

---

## 5. Scope of the Project

The scope of InVoice Maker includes the design and development of a cross-platform mobile application that provides complete invoice generation and business management features. The system is targeted at freelancers, small traders, shopkeepers, and micro-enterprises operating under GST regulations.

**The scope includes:**

- **Invoice Generation:** Create GST-compliant invoices with auto-calculated tax (CGST, SGST, IGST), discounts, and subtotals within seconds.
- **Invoice Management:** View, filter, and manage all past invoices with paid/unpaid status tracking.
- **Customer Management:** Add, edit, and retrieve customer profiles including GSTIN, address, phone, and email.
- **Item/Product Catalogue:** Maintain a product/service list with unit pricing and tax rates.
- **Sales Dashboard:** Real-time analytics showing monthly sales totals, paid invoice count, and unpaid invoice count, along with a list of recent invoices.
- **Invoice Preview & Export:** A professional, print-ready invoice preview with PDF export and sharing via platform share sheet.
- **Business Profile Management:** Configure business name, address, GSTIN, and logo for use on all generated invoices.
- **Language & Currency Settings:** Support for multiple languages (English, French, Spanish, Portuguese, Arabic, Bengali, Russian, German) and currencies.
- **Offline-First Architecture:** All operations are performed entirely offline using a local Room (SQLite) database.
- **Freemium Model:** A Paywall screen offering a Pro subscription for unlimited invoices, watermark removal, and advanced features.

The system is intended for individual business use and academic demonstration. It does not replace professional chartered accountant services or official tax filing systems.

---

## 6. Literature Review

Recent research highlights the growing demand for mobile-first business management tools, particularly in developing economies experiencing rapid GST adoption. Gupta et al. (2024) demonstrated that small businesses using digital invoicing tools experience a 40% reduction in billing errors compared to manual paper-based systems. Similarly, Verma and Sharma (2024) noted that offline-capable mobile applications are significantly more reliable for field-based traders and vendors operating in areas with inconsistent network connectivity.

Studies on cross-platform mobile development by Chen et al. (2025) confirmed that Kotlin Multiplatform (KMP) reduces development time by approximately 35–45% compared to maintaining separate native applications, while preserving native UI performance. Research by Patel et al. (2024) on MVVM architecture in Android applications highlighted improved code maintainability and testability, which aligns with the architectural approach adopted in InVoice Maker.

These studies collectively support the design decisions in this project: an offline-first, cross-platform, MVVM-based invoice application with a focus on usability, GST compliance, and business practicality. Existing tools such as Zoho Invoice, Vyapar, and QuickBooks lack lightweight cross-platform offline solutions, which this project uniquely addresses.

---

## 7. Methodology and Research Design

### 7.1 Research Design

The project follows an iterative **Agile development approach** with the following phases:

1. **Requirement Analysis** — Identifying functional and non-functional requirements through user research and competitive analysis.
2. **System Design** — Designing the database schema, navigation structure, UI wireframes, and component architecture.
3. **Development** — Implementing shared business logic, data layer, UI screens, and platform-specific integrations.
4. **Testing** — Unit testing (ViewModels, DAOs), integration testing, and manual UI testing on Android and iOS.
5. **Evaluation** — User testing, performance benchmarking, and feedback collection.

### 7.2 System Architecture

The application uses a clean **MVVM (Model-View-ViewModel)** architecture with a layered structure:

| Layer | Component |
|-------|-----------|
| **UI Layer** | Jetpack Compose Multiplatform Screens |
| **ViewModel Layer** | DashboardViewModel, InvoiceViewModel, BusinessViewModel, SettingsViewModel |
| **Domain Layer** | Business logic for invoice calculation (GST, discounts, totals) |
| **Data Layer** | Room DAOs (BusinessDao, CustomerDao, ItemDao, InvoiceDao) |
| **Database** | Room (SQLite) via BundledSQLiteDriver |
| **DI** | Koin for dependency injection |
| **Navigation** | Compose Navigation with sealed Screen routes |

### 7.3 Application Screens

| Screen | Purpose |
|--------|---------|
| `WelcomeScreen` | Onboarding and app entry point |
| `BusinessSetupScreen` | Business profile configuration |
| `DashboardScreen` | Sales analytics and recent invoices |
| `InvoiceCreateScreen` | Step-by-step invoice creation with GST calculation |
| `InvoicePreviewScreen` | Professional invoice preview with PDF export |
| `InvoicesListScreen` | Full list of all past invoices |
| `CustomersScreen` | Customer management (add/view/delete) |
| `ItemsScreen` | Product/service catalogue management |
| `SettingsScreen` | Languages, currency, business profile, legal links |
| `LanguageSelectionScreen` | Multi-language and multi-currency picker |
| `PaywallScreen` | Freemium Pro subscription offering |

### 7.4 Database Schema

| Table | Key Fields |
|-------|-----------|
| `businesses` | id, name, address, gstin, phone, email, logo |
| `customers` | id, name, gstin, address, phone, email |
| `items` | id, name, description, unit, price, taxRate |
| `invoices` | id, invoiceNumber, date, customerId, businessId, subTotal, cgst, sgst, igst, totalAmount, discount, isPaid, notes |
| `invoice_items` | id, invoiceId, itemId, name, quantity, price, taxRate, amount |

---

## 8. Tools and Technologies

| Category | Technology |
|----------|-----------|
| **Programming Language** | Kotlin (Kotlin Multiplatform) |
| **UI Framework** | Jetpack Compose Multiplatform |
| **Architecture Pattern** | MVVM (Model-View-ViewModel) |
| **Database** | Room Database (SQLite via BundledSQLiteDriver) |
| **Dependency Injection** | Koin |
| **Navigation** | Jetpack Navigation Compose |
| **Date/Time** | kotlinx-datetime |
| **Asynchronous Programming** | Kotlin Coroutines & Flow |
| **Preferences Storage** | DataStore (multi-platform) |
| **PDF / Export** | Platform-specific PDF generation (Android PrintManager / iOS) |
| **Development IDE** | Android Studio, Xcode |
| **Version Control** | Git / GitHub |
| **Build System** | Gradle (Kotlin DSL) |
| **Testing** | JUnit, Kotlin Test |

---

## 9. Expected Outcomes

- A fully functional cross-platform (Android & iOS) GST invoice generator application.
- Professional PDF invoice generation and sharing capability.
- Complete offline operation with local data storage — no internet required.
- An intuitive, modern UI supporting 8+ languages and multiple currencies.
- A reusable, maintainable shared codebase using Kotlin Multiplatform (KMP).
- Practical experience with cross-platform mobile development, MVVM architecture, and Room database.
- A deployable mobile application suitable for real-world small business use.
- A validated freemium monetization model through the Pro subscription paywall.

---

## 10. Timeline

| Phase | Activities | Duration |
|-------|-----------|----------|
| **Phase 1** | Requirement Analysis, Competitive Analysis, UI/UX Wireframes | Weeks 1–2 |
| **Phase 2** | Database Schema Design, Koin DI Setup, Navigation Structure | Weeks 3–4 |
| **Phase 3** | Business Setup, Customer & Item Management Screens | Weeks 5–6 |
| **Phase 4** | Invoice Creation, GST Calculation Engine, Invoice Preview | Weeks 7–9 |
| **Phase 5** | Dashboard Analytics, Invoice List, Settings & Language Support | Weeks 10–11 |
| **Phase 6** | PDF Export, Share Feature, Paywall Integration | Week 12 |
| **Phase 7** | Testing (Unit + Integration + UI), Bug Fixes | Weeks 13–14 |
| **Phase 8** | Final Build, Documentation, Presentation Preparation | Weeks 15–16 |

---

## 11. Conclusion

InVoice Maker aims to provide a fast, reliable, and legally compliant invoice generation solution for small businesses and freelancers through an intuitive cross-platform mobile application. By combining Kotlin Multiplatform with modern Android architecture components such as Jetpack Compose, Room Database, and MVVM, the project demonstrates how a single shared codebase can deliver a premium, production-ready experience on both Android and iOS platforms. The offline-first design ensures usability even in low-connectivity environments, while the multi-language and multi-currency support broadens the app's global applicability. This project serves as both a practical tool for real-world business use and a demonstration of advanced cross-platform mobile development skills developed during the final year of the software engineering programme.

---

## 12. References

[1] S. Gupta, R. Mehta, and A. Singh, "Impact of digital invoicing on billing accuracy in small businesses," *International Journal of Business Technology*, vol. 12, no. 3, pp. 45–53, 2024.

[2] R. Verma and P. Sharma, "Offline-first mobile applications for field-based business operations," *Journal of Mobile Computing and Applications*, vol. 8, no. 1, pp. 110–119, 2024.

[3] L. Chen, W. Zhang, and M. Liu, "Kotlin Multiplatform: A comparative study on cross-platform mobile development efficiency," *International Journal of Software Engineering and Applications*, vol. 16, no. 2, pp. 33–48, 2025.

[4] A. Patel, S. Joshi, and K. Rao, "MVVM architecture pattern for improved testability in Android applications," *Journal of Systems and Software*, vol. 197, pp. 111–124, 2024.

[5] Google Developers, "Jetpack Compose Multiplatform documentation," 2025. [Online]. Available: https://developer.android.com/jetpack/compose

[6] JetBrains, "Kotlin Multiplatform documentation," 2025. [Online]. Available: https://kotlinlang.org/docs/multiplatform.html

[7] Android Developers, "Room Persistence Library documentation," 2025. [Online]. Available: https://developer.android.com/training/data-storage/room

[8] JetBrains, "Koin — Kotlin dependency injection framework," 2025. [Online]. Available: https://insert-koin.io/

---

*Submitted to: Department of Software Engineering, Sarhad University of Science & Information Technology, Peshawar*
*Date: March 2026*
