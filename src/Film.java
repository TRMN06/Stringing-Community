public abstract class Film {

    String nome,link,img_nome,genre;
    boolean visuallizato,guarda_dopo;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Film(String nome, String link, String img_nome, String genre) {
        this.nome = nome;
        this.link = link;
        this.img_nome = img_nome;
        this.genre = genre;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg_nome() {
        return img_nome;
    }

    public void setImg_nome(String img_nome) {
        this.img_nome = img_nome;
    }

    public boolean isVisuallizato() {
        return visuallizato;
    }

    public void setVisuallizato(boolean visuallizato) {
        this.visuallizato = visuallizato;
    }
}
