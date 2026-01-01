// Ali Oturak 243405116 Musharraf Ahmed Osman 233405002

public class Urun {
    private int id;
    private String ad;
    private String kategori;
    private double fiyat;
    private int stokAdedi;
    private String aciklama;

    public Urun(int id, String ad, String kategori, double fiyat, int stokAdedi, String aciklama) {
        this.id = id;
        this.ad = ad;
        this.kategori = kategori;
        this.fiyat = fiyat;
        this.stokAdedi = stokAdedi;
        this.aciklama = aciklama;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public double getFiyat() { return fiyat; }
    public void setFiyat(double fiyat) { this.fiyat = fiyat; }

    public int getStokAdedi() { return stokAdedi; }
    public void setStokAdedi(int stokAdedi) { this.stokAdedi = stokAdedi; }

    public String getAciklama() { return aciklama; }
    public void setAciklama(String aciklama) { this.aciklama = aciklama; }
}