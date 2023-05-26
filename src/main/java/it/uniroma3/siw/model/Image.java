package it.uniroma3.siw.model;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;


    private String imagePath;

	private String type;


    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;	

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Movie getMovie() {
		return movie;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, imagePath, movie, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Image other = (Image) obj;
		return Objects.equals(id, other.id) && Objects.equals(imagePath, other.imagePath)
				&& Objects.equals(movie, other.movie) && Objects.equals(name, other.name);
	}
	

}
