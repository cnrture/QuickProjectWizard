# QuickProjectWizard

![Build](https://github.com/cnrture/QuickProjectWizard/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/25221.svg)](https://plugins.jetbrains.com/plugin/25221)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/25221.svg)](https://plugins.jetbrains.com/plugin/25221)

<img title="" src="../src/main/resources/META-INF/pluginIcon.svg" alt="QuickProjectWizard" width="144">

[Dokümantasyon](https://quickprojectwizard.candroid.dev/tutorials/project-wizard/)

<!-- Plugin description -->

#### Android geliştirme sürecinizin vazgeçilmez yardımcısı: Proje kurulumunu kolaylaştıran, güçlü geliştirme araçları sunan bu araç sayesinde modern bağımlılıklarla yeni projeler oluşturabilir, modülleri ve özellikleri yönetebilir, temel araçlara tek bir sezgisel arayüzden erişebilirsiniz.

🚀 **Proje Şablonları** • 🏗️ **Modül & Feature Geliştirme** • 🎨 **Geliştirme Araçları** • ⚙️ **Takım Çalışması**

<!-- Plugin description end -->

## ✨ Özelliklere Genel Bakış

### 🚀 Proje Oluşturma

- **Compose & XML Şablonları**: En güncel bağımlılıklar ile modern Android proje kurulumu
- **Compose Multiplatform**: Android, iOS, Masaüstü ve Web için çok platformlu projeler
- **Akıllı Bağımlılık Yönetimi**: Otomatik kütüphane entegrasyonu ve yapılandırması
- **Clean Architecture**: En iyi uygulamalarla önceden yapılandırılmış proje yapısı

### 🏗️ Modül & Feature Yönetimi

- **Modül Oluşturucu**: Özelleştirilebilir şablonlarla yeni modüller oluştur
- **Feature Scaffolding**: Veri, domain ve UI katmanlarıyla birlikte tüm özellikleri oluştur
- **Code Migration**: Mevcut dosyaları doğru modül yapısına taşı
- **Şablon Sistemi**: Tutarlı kod üretimi için özel şablonlar oluştur

### 🎨 Geliştirme Araçları

- **Sistem Renk Seçicisi**: Büyüteçli profesyonel renk seçici
- **JSON/XML Biçimlendiricisi**: Sözdizimi vurgusu ile veri biçimlendirme ve doğrulama
- ****API Test Aracı**: IDE içerisinden API’leri test edebileceğin HTTP istemcisi
- **Dosya Ağacı Tarayıcısı**: Görsel dizin gezgini ve seçim aracı

### ⚙️ Ayarlar ve Yapılandırma

- **Şablon Yönetimi**: Özel şablonlar oluştur, düzenle ve paylaş
- **Ayar Senkronizasyonu**: Güncellemeler arasında otomatik yedekleme ve geri yükleme
- **Takım Çalışması**: Tutarlı takım kurulumları için ayar paylaşımı
- **Cross-Platform Desteği**: Windows, macOS ve Linux ile sorunsuz çalışır

---

## 📦 Kurulum

<details>
<summary><b>🛍️ JetBrains Marketplace Üzerinden Kurulum (Tavsiye Edilen)</b></summary>

1. Android Studio'yu aç
2. **Settings/Preferences** → **Plugins** → **Marketplace** bölümüne git
3. **"QuickProjectWizard"** şeklinde arat**
4. **Yükle (Install)** butonuna tıkla ve IDE’yi yeniden başlat

Ya da doğrudan [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/25221-quickprojectwizard) sayfasını ziyaret edebilirsin.

</details>

<details>
<summary><b>📁 Manuel Kurulum</b></summary>

1. [Son sürümü indir](https://github.com/cnrture/QuickProjectWizard/releases/latest)
2. **Settings/Preferences** → **Plugins** → **⚙️** → **Install plugin from disk** adımlarını takip et
3. İndirdiğin dosyayı seç ve IDE’yi yeniden başlat

</details>

---

## 🎯 Hızlı Başlangıç Rehberi

### 1️⃣ Yeni Proje Oluşturma

#### 🎨 Compose Projeleri

<img title="" src="../images/new_project.png" alt="New Compose Project" width="600">

#### 📱 XML Projeleri

<img title="" src="../images/new_project.png" alt="New XML Project" width="600">

#### 🌍 Compose Multiplatform

<img title="" src="../images/new_project_cmp.png" alt="Compose Multiplatform" width="600">

**Adımlar:**

1. **File** → **New** → **Project**
2. **Quick Project Wizard**’ı seç
3. İstediğin şablonu seç (Compose / XML / Multiplatform)
4. Proje bilgilerini ve bağımlılıkları yapılandır
5. **Finish (Bitir)** butonuna tıkla ve kodlamaya başla! 🚀

### 2️⃣ Geliştirme Araçlarına Erişim

Araç penceresini (Tool Window) aç ve şu adımları takip et: **View** → **Tool Windows** → **QuickProjectWizard**

<img title="" src="../images/final.png" alt="Tool Window" width="600">

---

## 🛠️ Geliştirme Araçlarına Derinlemesine Bakış

### 🏗️ Modül Oluşturucu

Projendeki modülleri verimli şekilde oluştur ve düzenle:

**Özellikler:**

- ✅ **Yeni Modül Oluşturma**: Temiz mimariye uygun modüller oluştur
- ✅ **Dosya Taşıma**: Mevcut kodu doğru modül yapısına taşı
- ✅ **Bağımlılık Tespiti**: Kütüphane bağımlılıklarını otomatik çöz
- ✅ **Şablon Özelleştirme**: Hazır ya da özel şablonları kullan
- ✅ **Çoklu Modül Desteği**: Karmaşık proje yapılarıyla uyumlu

**Kullanım:**

1. QuickProjectWizard araç penceresini aç
2. **Modül Üretici (Module Generator)** sekmesine geç
3. **Yeni Oluştur (Create New)** veya **Mevcut Dosyaları Taşı (Move Existing Files)** seçeneklerinden birini seç
4. Modül ayarlarını yap ve bağımlılıkları seç
5. Tek tıkla modülünü oluştur!

### ⚡ Feature Oluşturucu

Tutarlı mimariyle tam özellik setlerini hızlıca oluştur:

**Oluşturulan Yapı:**

```
feature/
├── data/
│   ├── repository/
│   ├── datasource/
│   └── dto/
├── domain/
│   ├── usecase/
│   ├── repository/
│   └── model/
└── presentation/
    ├── ui/
    ├── viewmodel/
    └── contract/
```

**Avantajlar:**

- 🎯 **MVVM Mimarisi**: Sorumlulukların net ayrımı
- 🔄 **Compose Entegrasyonu**: Durum yönetimiyle modern UI
- 📝 **Özel Şablonlar**: Kod stiline göre şablon düzenleme
- 🚀 **Hızlı Geliştirme**: Saniyeler içinde özellik oluşturma

### 🎨 Renk Seçici

IDE’ye entegre profesyonel renk seçme aracı:

**Özellikler:**

- 🔍 **Sistem Genelinde Seçim**: Ekrandaki her yerden renk al
- 🔎 **Büyüteç**: Piksel hassasiyetinde yakınlaştırma
- 📋 **Çoklu Formatlar**: HEX, RGB, HSV veya HSL kopyalama
- 📚 **Renk Geçmişi**: Son kullanılan renklere hızlı erişim
- ⌨️ **Klavye Kısayolları**: Verimli çalışma akışı

**Şunlar için ideal:**

- UI/UX renk paleti oluşturma
- Tasarımlardan renk eşleştirme
- Marka renk uyumluluğu
- Material Design paleti hazırlama

### 🧰 JSON/XML Biçimlendiricisi

Verini profesyonel araçlarla biçimlendir ve doğrula:

**Yetenekler:**

- ✨ **Akıllı Biçimlendirme**: Doğru girintileme ve yapı
- ✅ **Syntax Doğrulama**: Gerçek zamanlı hata tespiti
- 🎨 **Syntax Vurgulama**: Daha iyi okunabilirlik için renk kodlama
- 💾 **State Persistence**: İçeriğini oturumlar arasında hatırlama
- ⚡ **Anlık İşleme**: Yazarken anında biçimlendirme

### 🌐 API Test Aracı

IDE'den çıkmadan API testleri yapabileceğin dahili HTTP istemcisi:

**Özellikler:**

- 🔗 **Tüm HTTP Metotları**: GET, POST, PUT, DELETE, PATCH vb.
- 📝 **İstek Oluşturucu**: Başlıklar, parametreler, gövde içeriği
- 📊 **Yanıt Analizi**: Durum kodları, başlıklar, biçimlendirilmiş yanıt
- 📚 **İstek Geçmişi**: Önceki isteklere hızlı erişim
- 💾 **Oturum Kaydı**: IDE yeniden başlatılsa bile durum korunur

**Örnek API Endpointleri:**

- Weather API: `https://api.openweathermap.org/data/2.5/weather`
- JSONPlaceholder: `https://jsonplaceholder.typicode.com/posts`
- Harry Potter API: `https://api.canerture.com/harrypotterapp/characters`

---

## ⚙️ Ayarlar ve Özelleştirme

### 📋 Şablon Yönetimi

Tutarlı kod üretimi için özel şablonlar oluştur ve yönet:

**Modül Şablonları:**

- Dosya yapısını ve içeriğini özelleştir
- Placeholder kullan: `{NAME}`, `{PACKAGE}`, `{FILE_PACKAGE}`
- Şablonları takımınla paylaş
- Şablon koleksiyonlarını içe/dışa aktar

**Şablon Yer Tutucular:**

- `{NAME}` → Dosya adı (extension olmadan)
- `{PACKAGE}` → Temel paket adı (örnek: `com.example.app`)
- `{FILE_PACKAGE}` → Tam paket yolu (örnek: `com.example.app.feature.home`)

### 🔄 Ayar Senkronizasyonu

Yapılandırmalarını asla kaybetme:

**Auto-Backup System:**

- ✅ **Otomatik Dışa Aktarma**: Her ayar değişikliğinde yedek alınır
- ✅ **Cross-Platform**: Windows, macOS ve Linux'ta çalışır
- ✅ **Güncellemeye Dayanıklı**: Eklenti güncellense bile ayarlar korunur
- ✅ **Takım Paylaşımı**: Takım içinde ayar dışa/İçe aktarımıyla tutarlılık sağla

**Yedekleme Konumu:**

- **Windows**: `C:\Users\{username}\.quickprojectwizard\settings.json`
- **macOS**: `/Users/{username}/.quickprojectwizard/settings.json`
- **Linux**: `/home/{username}/.quickprojectwizard/settings.json`

### 👥 Takım Çalışması

Yapılandırmaları geliştirme ekibinle paylaş:

1. **Ayarları Dışa Aktar**: Kendi ayarlarından bir yapılandırma dosyası üret
2. **Dosyayı Paylaş**: Git, Slack veya e-posta yoluyla gönder
3. **Ayarları İçe Aktar**: Takım arkadaşların senin ayarlarını içe aktarır
4. **Tutarlı Kurulum**: Herkes aynı şablonları ve tercihleri kullanır

---

## 🚀 İleri Düzey Kullanım

### 🎯 Özel Proje Şablonları

Kendi proje şablonlarını oluştur:

1. Tercih ettiğin yapı ile bir proje oluştur
2. Özel modül ve özellik şablonları hazırla
3. Varsayılan ayarları ve bağımlılıkları yapılandır
4. Dışa aktar ve takımınla paylaş

### 🔧 Mevcut Projelerle Entegrasyon

QuickProjectWizard’ı mevcut projelere ekle:

1. Yeni modüller oluşturmak için **Modül Üreticiyi (Module Generator)** kullan
2. Mevcut kodu **File Migration** aracıyla taşı
3. Tutarlı mimaride yeni özellikler üret
4. Özel şablonlarla kod kalitesini koru

### ⚡ Verimlilik İpuçları

**Klavye Kısayolları:**

- Araç penceresine hızlı erişim
- Şablonları hızlıca uygulama
- Renk seçme işlemlerini hızlandırma

**En İyi Uygulamalar:**

- Tutarlı adlandırma kuralları kullan
- Şablon yer tutucularını verimli kullan
- Takım şablon kütüphaneleri oluştur
- Ayarlarını düzenli olarak yedekle ve senkronize et

---

## 🤝 Katkıda Bulunma

Katkılarınızı memnuniyetle kabul ediyoruz! İşte nasıl yardımcı olabileceğiniz:

- 🐛 **Hata Bildirimi**: [Yeni bir sorun oluşturun](https://github.com/cnrture/QuickProjectWizard/issues)
- 💡 **Özellik Önerisi**: Geliştirme fikirlerinizi paylaşın
- 🔧 **Kod Katkısı**: Pull request gönderin
- 📖 **Dokümantasyon**: Kılavuzları ve öğreticileri iyileştirmeye yardımcı olun
- ⭐ **Destek Verin**: Repostoyu yıldızlayın ve başkalarıyla paylaşın

---

## 📄 Lisans

Bu proje MIT Lisansı ile lisanslanmıştır – detaylar için [LICENSE](LICENSE) dosyasına bakabilirsiniz.

---

## 🙏 Teşekkür

- [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template) ile geliştirildi
- Android geliştirici topluluğundan ilham alındı
- Geri bildirim veren tüm katkıcılara ve kullanıcılara teşekkürler

---

## Diğer Dil Seçenekleri

[![English](https://img.shields.io/badge/EN-◯-green.svg)](/)
[![Turkish](https://img.shields.io/badge/TR-⚫-green.svg)]()

---

## 📞 Destek

- 📧 **Email**: cnrture@gmail.com
- 🌐 **Website**: [canerture.com](https://canerture.com)
- 💬 **Tartışmalar**: [GitHub Discussions](https://github.com/cnrture/QuickProjectWizard/discussions)
- 🐛 **Hatalar**: [GitHub Issues](https://github.com/cnrture/QuickProjectWizard/issues)

---

<div align="center">

❤️ [Caner Türe](https://github.com/cnrture) tarafından sevgiyle geliştirildi

[⭐ Bu projeyi yıldızla](https://github.com/cnrture/QuickProjectWizard) • [🍴 Forkla](https://github.com/cnrture/QuickProjectWizard/fork) • [📥 İndir](https://plugins.jetbrains.com/plugin/25221-quickprojectwizard)

</div>
