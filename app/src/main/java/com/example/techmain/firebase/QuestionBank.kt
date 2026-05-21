package com.example.techmain.firebase

object QuestionBank {
    data class Category(val id: String, val name: String, val icon: String)

    val categories = listOf(
        Category("math", "Matematika", "calculate"),
        Category("science", "IPA", "biotech"),
        Category("history", "Sejarah", "history"),
        Category("geography", "Geografi", "public"),
        Category("language", "Bahasa", "translate"),
        Category("general", "Pengetahuan Umum", "lightbulb")
    )

    fun getQuestions(categoryId: String): List<QuizQuestion> {
        return when (categoryId) {
            "math" -> listOf(
                QuizQuestion(id = "m1", categoryId = "math", question = "Berapa hasil dari 25 × 4?", options = listOf("75", "100", "125", "150"), correctAnswer = 1),
                QuizQuestion(id = "m2", categoryId = "math", question = "Akar kuadrat dari 144 adalah?", options = listOf("10", "11", "12", "13"), correctAnswer = 2),
                QuizQuestion(id = "m3", categoryId = "math", question = "Berapa hasil dari ⅓ + ⅙?", options = listOf("¼", "⅓", "½", "⅔"), correctAnswer = 2),
                QuizQuestion(id = "m4", categoryId = "math", question = "Jika x + 5 = 12, berapa nilai x?", options = listOf("5", "6", "7", "8"), correctAnswer = 2),
                QuizQuestion(id = "m5", categoryId = "math", question = "Berapa luas lingkaran dengan jari-jari 7 cm? (π = 22/7)", options = listOf("144 cm²", "154 cm²", "164 cm²", "174 cm²"), correctAnswer = 1),
                QuizQuestion(id = "m6", categoryId = "math", question = "45% dari 200 adalah?", options = listOf("70", "80", "90", "100"), correctAnswer = 2),
                QuizQuestion(id = "m7", categoryId = "math", question = "Berapa sisi yang dimiliki kubus?", options = listOf("4", "6", "8", "12"), correctAnswer = 1),
                QuizQuestion(id = "m8", categoryId = "math", question = "Hasil dari (-3) × (-4) adalah?", options = listOf("-12", "-7", "7", "12"), correctAnswer = 3),
            )
            "science" -> listOf(
                QuizQuestion(id = "s1", categoryId = "science", question = "Apa planet terbesar di tata surya?", options = listOf("Saturnus", "Jupiter", "Neptunus", "Uranus"), correctAnswer = 1),
                QuizQuestion(id = "s2", categoryId = "science", question = "Apa simbol kimia untuk air?", options = listOf("H2O", "CO2", "NaCl", "O2"), correctAnswer = 0),
                QuizQuestion(id = "s3", categoryId = "science", question = "Berapa jumlah tulang manusia dewasa?", options = listOf("106", "206", "306", "406"), correctAnswer = 1),
                QuizQuestion(id = "s4", categoryId = "science", question = "Apa gas yang paling banyak di atmosfer bumi?", options = listOf("Oksigen", "Karbon dioksida", "Nitrogen", "Hidrogen"), correctAnswer = 2),
                QuizQuestion(id = "s5", categoryId = "science", question = "Fotosintesis menghasilkan?", options = listOf("CO2 dan Air", "Oksigen dan Glukosa", "Nitrogen dan Protein", "Karbon dan Mineral"), correctAnswer = 1),
                QuizQuestion(id = "s6", categoryId = "science", question = "Berapa kecepatan cahaya per detik?", options = listOf("100.000 km", "200.000 km", "300.000 km", "400.000 km"), correctAnswer = 2),
                QuizQuestion(id = "s7", categoryId = "science", question = "Apa organ terbesar dalam tubuh manusia?", options = listOf("Hati", "Paru-paru", "Kulit", "Otak"), correctAnswer = 2),
                QuizQuestion(id = "s8", categoryId = "science", question = "Apa nama proses perubahan air menjadi uap?", options = listOf("Kondensasi", "Evaporasi", "Presipitasi", "Sublimasi"), correctAnswer = 1),
            )
            "history" -> listOf(
                QuizQuestion(id = "h1", categoryId = "history", question = "Tahun berapa Indonesia merdeka?", options = listOf("1942", "1945", "1949", "1950"), correctAnswer = 1),
                QuizQuestion(id = "h2", categoryId = "history", question = "Siapa presiden pertama Indonesia?", options = listOf("Soeharto", "Soekarno", "Hatta", "Sutan Syahrir"), correctAnswer = 1),
                QuizQuestion(id = "h3", categoryId = "history", question = "Perang Dunia II berakhir tahun?", options = listOf("1943", "1944", "1945", "1946"), correctAnswer = 2),
                QuizQuestion(id = "h4", categoryId = "history", question = "Siapa yang menemukan benua Amerika?", options = listOf("Vasco da Gama", "Ferdinand Magellan", "Christopher Columbus", "Marco Polo"), correctAnswer = 2),
                QuizQuestion(id = "h5", categoryId = "history", question = "Candi Borobudur terletak di provinsi?", options = listOf("Jawa Barat", "Jawa Tengah", "Jawa Timur", "Yogyakarta"), correctAnswer = 1),
                QuizQuestion(id = "h6", categoryId = "history", question = "Tembok Besar Cina dibangun pada dinasti?", options = listOf("Ming", "Qin", "Tang", "Song"), correctAnswer = 1),
                QuizQuestion(id = "h7", categoryId = "history", question = "Apa nama kerajaan Hindu pertama di Indonesia?", options = listOf("Majapahit", "Sriwijaya", "Kutai", "Singhasari"), correctAnswer = 2),
                QuizQuestion(id = "h8", categoryId = "history", question = "Siapa pahlawan nasional dari Aceh?", options = listOf("Diponegoro", "Cut Nyak Dhien", "Imam Bonjol", "Pattimura"), correctAnswer = 1),
            )
            "geography" -> listOf(
                QuizQuestion(id = "g1", categoryId = "geography", question = "Apa ibu kota Indonesia?", options = listOf("Surabaya", "Bandung", "Jakarta", "Yogyakarta"), correctAnswer = 2),
                QuizQuestion(id = "g2", categoryId = "geography", question = "Gunung tertinggi di dunia adalah?", options = listOf("Kilimanjaro", "Everest", "Elbrus", "Aconcagua"), correctAnswer = 1),
                QuizQuestion(id = "g3", categoryId = "geography", question = "Sungai terpanjang di dunia?", options = listOf("Amazon", "Nil", "Mississippi", "Yangtze"), correctAnswer = 1),
                QuizQuestion(id = "g4", categoryId = "geography", question = "Berapa jumlah provinsi di Indonesia saat ini?", options = listOf("34", "37", "38", "40"), correctAnswer = 2),
                QuizQuestion(id = "g5", categoryId = "geography", question = "Benua terluas di dunia?", options = listOf("Afrika", "Amerika Utara", "Asia", "Eropa"), correctAnswer = 2),
                QuizQuestion(id = "g6", categoryId = "geography", question = "Laut terluas di dunia?", options = listOf("Atlantik", "Pasifik", "Hindia", "Arktik"), correctAnswer = 1),
                QuizQuestion(id = "g7", categoryId = "geography", question = "Negara dengan populasi terbanyak di dunia?", options = listOf("India", "Cina", "AS", "Indonesia"), correctAnswer = 0),
                QuizQuestion(id = "g8", categoryId = "geography", question = "Gurun terluas di dunia?", options = listOf("Gobi", "Sahara", "Kalahari", "Arab"), correctAnswer = 1),
            )
            "language" -> listOf(
                QuizQuestion(id = "l1", categoryId = "language", question = "\"Good morning\" dalam bahasa Indonesia adalah?", options = listOf("Selamat sore", "Selamat pagi", "Selamat malam", "Selamat siang"), correctAnswer = 1),
                QuizQuestion(id = "l2", categoryId = "language", question = "Sinonim dari \"cerdas\" adalah?", options = listOf("Bodoh", "Pandai", "Malas", "Lemah"), correctAnswer = 1),
                QuizQuestion(id = "l3", categoryId = "language", question = "Bahasa resmi negara Brasil adalah?", options = listOf("Spanyol", "Portugis", "Inggris", "Prancis"), correctAnswer = 1),
                QuizQuestion(id = "l4", categoryId = "language", question = "Antonim dari \"besar\" adalah?", options = listOf("Lebar", "Panjang", "Kecil", "Tinggi"), correctAnswer = 2),
                QuizQuestion(id = "l5", categoryId = "language", question = "Apa arti \"pulchritudinous\"?", options = listOf("Cepat", "Indah", "Kuat", "Tua"), correctAnswer = 1),
                QuizQuestion(id = "l6", categoryId = "language", question = "Bahasa apa yang paling banyak digunakan di dunia?", options = listOf("Inggris", "Mandarin", "Spanyol", "Hindi"), correctAnswer = 1),
                QuizQuestion(id = "l7", categoryId = "language", question = "Kata \"demokrasi\" berasal dari bahasa?", options = listOf("Latin", "Yunani", "Inggris", "Arab"), correctAnswer = 1),
                QuizQuestion(id = "l8", categoryId = "language", question = "Apa imbuhan yang menunjukkan \"telah melakukan\"?", options = listOf("Me-", "Ber-", "Ter-", "Di-"), correctAnswer = 2),
            )
            else -> listOf(
                QuizQuestion(id = "u1", categoryId = "general", question = "Berapa hari dalam setahun?", options = listOf("360", "364", "365", "366"), correctAnswer = 2),
                QuizQuestion(id = "u2", categoryId = "general", question = "Apa warna bendera Indonesia?", options = listOf("Merah-Putih", "Merah-Biru", "Putih-Hijau", "Kuning-Hitam"), correctAnswer = 0),
                QuizQuestion(id = "u3", categoryId = "general", question = "Alat untuk mengukur suhu disebut?", options = listOf("Barometer", "Termometer", "Hidrometer", "Speedometer"), correctAnswer = 1),
                QuizQuestion(id = "u4", categoryId = "general", question = "Hewan apa yang dikenal sebagai \"raja hutan\"?", options = listOf("Harimau", "Singa", "Gajah", "Beruang"), correctAnswer = 1),
                QuizQuestion(id = "u5", categoryId = "general", question = "Berapa jumlah warna dalam pelangi?", options = listOf("5", "6", "7", "8"), correctAnswer = 2),
                QuizQuestion(id = "u6", categoryId = "general", question = "Mata uang Indonesia adalah?", options = listOf("Ringgit", "Rupiah", "Baht", "Peso"), correctAnswer = 1),
                QuizQuestion(id = "u7", categoryId = "general", question = "Olahraga sepak bola dimainkan oleh berapa pemain per tim?", options = listOf("9", "10", "11", "12"), correctAnswer = 2),
                QuizQuestion(id = "u8", categoryId = "general", question = "Planet apa yang dikenal sebagai \"Bintang Fajar\"?", options = listOf("Mars", "Venus", "Merkurius", "Jupiter"), correctAnswer = 1),
            )
        }.shuffled().take(5)
    }
}
