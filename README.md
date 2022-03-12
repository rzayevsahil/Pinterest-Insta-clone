# Pinterest-Insta-clone

***Pinterest tarzında instagram klonu***



![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 001](https://user-images.githubusercontent.com/58303745/158010866-8d905187-5b99-4950-bbee-280278505b08.png)

**Mobil Uygulama Geliştirme** 

Öğrenci : ***Sahil Rzayev 399973*** 

Proje adı : ***Instagram Clone*** 

**Uygulama açıklaması** 

Genel görüntü olarak günümüzde popüler olan Instagram uygulamasına benzetmeye çalıştım.  Uygulamanın özelliklerine bakarsak sağ üst köşede *paylaşım(yani resim) ekleme* ve *hesaptan çıkış yapma* butonları yer alıyor. Alt kısımda yer alan bar’da *ana sayfa* ve *profil güncelleme* menüleri mevcut.  

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 002](https://user-images.githubusercontent.com/58303745/158010867-20ce892e-cc04-4d30-a494-bc5aef4e2cd5.png)

Tasarım olarak dikkat etdiğimizde paylaşım yapan kullanıcıların profil resimleri ve kullanıcı isimleri aynı hizada paylaştıkları fotoğrafın üzerinde yer alıyor. Fotoğrafın alt kısmında ise kullanıcının paylaşımı paylaştığı zaman ona eklediği comment yeralıyor. 

Bu kısım gönderi paylaşma kısmı. Fotoğraf seçmeden upload butonuna basarsak resim seçmedik diye bize uyarı mesajı gösterecektir.  
![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 003](https://user-images.githubusercontent.com/58303745/158010870-4888095f-17d1-458c-8a64-6aa67d469d70.png)

Resmi seçdikten sonra comment ekleyip eklememek kullanıcının isteğine bırakılmış(yani mecburi değil).  

Upload butonun tıkladığında ana sayfaya yönlendiriliyor ve paylaşdığı fotoğraf da ana sayfada gözüküyor. 

Burası kullanıcının profilini güncellediği kısım. Update butonuna basınca profilini güncelliyor ve ana sayfaya yönlendiriliyor. *back* - ana sayfaya dönme butonu.

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 004](https://user-images.githubusercontent.com/58303745/158010873-b8a0bc60-452a-4a4c-b150-c94cf8b8d46e.png)

Burası kullanıcınıdaha önce var olan bir hesabı yoksa kayıt olma yeri.  
![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 005](https://user-images.githubusercontent.com/58303745/158010874-0312c9e6-45fa-48ef-a0d0-5fbc5e97ebdc.png)

Burada profil fotoğrafı, kullanıcı adı, email, ve şifresini yazarak uygulamaya kayıt oluyor. Butona tıkladığı zaman eklediği resim boyutuna ve internet hızına göre belli bir süre gecikme oluyor(firebase’de storage kısmına ekleme işlemi yapılıyor bu süre çünkü) ve sonra direk ana sayfaya yönlendiriliyor. 

**Uygulama kodları açıklaması** 

Not: Uygulama kodları içinde birçok yerde yorum satırı ekledim. 

*Model* – burada sınıflar mevcut. *Adapter* – adapter yer alıyor. *View* – Activity’ler yer alıyor. 

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 006](https://user-images.githubusercontent.com/58303745/158010875-b8cb9811-e9cb-4c3f-a41e-bdcd3206a33d.png)

*Layout* – Genel görüntü xml’leri yer alıyor 

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 007](https://user-images.githubusercontent.com/58303745/158010876-54347456-5c06-471b-91b2-d63dca801a0e.png)

*Menu* 

- *option\_menu.xml* – uygulamadaki sağ üst köşedeki iconlar burada yer alıyor 
- *bottombar\_menu.xml* – uygulamada alt kısımda yer alan menü burada tanımlanıyor 

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 008](https://user-images.githubusercontent.com/58303745/158010877-44367a57-ad0c-4f06-a8e5-e5d78c91777e.png)
- *MainActivity* – Giriş yapma olayı burada yapılıyor 
- *SignUpActivity* – Kayıt olma işlemleri burada yapılıyor 
- *FeedActivity* – Ana sayfa yani paylaşımların tümünün gözüktüğü ekran 
- *UpdateProfileActivity* – Profil güncelleme kısmı 
- *UploadActivity* – Paylaşım paylaşma kısmı 

•  *PostAdapter* – Gelen verileri(yani kullanıcıların paylaştığı paylaşımlar) adapter sayesinde ana ekrana aktarıyoruz. 

- *Post* – Uygulama içinde paylaşılan paylaşımların sınıfı* 

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 009](https://user-images.githubusercontent.com/58303745/158010879-91ee6175-64c5-4b16-b5f0-89e7ad7d18ca.png)

- *Profile* – Kullanıcı profili sınıfı 

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 010](https://user-images.githubusercontent.com/58303745/158010880-566862e4-ce1e-4a92-bd30-996b5ced399c.png)

- *Implementasyonlar*: 

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 011](https://user-images.githubusercontent.com/58303745/158010881-b3e5de5b-473c-45d7-b527-9f1288ead55c.png)

- Genel olarak kullandığım yapılardan biri de ***View binding*** yapısıdır. View’larla etkileşime giren kodu daha kolay yazmamıza olanak sağlıyor. Aynı zamanda bu yapıyla fazla kod satırlarının önüne geçmiş oluyoruz. Örnek: 

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 011](https://user-images.githubusercontent.com/58303745/158010881-b3e5de5b-473c-45d7-b527-9f1288ead55c.png)

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 012](https://user-images.githubusercontent.com/58303745/158010883-424193ed-ac26-411c-bfd7-85fc6d2e6889.png)

![Aspose Words 7eb922a5-0fcb-471b-a9ec-2289088f63d3 013](https://user-images.githubusercontent.com/58303745/158010885-334f9ca2-68ff-425f-a6b2-4b4687ff6824.png)

*Not: Kod satırları çok fazla olduğu için hepsinin fotoğrafını buraya ekleyemedim. Ama birçok yerde anlaşılır olsun diye yorum satırları bıraktım.* 
