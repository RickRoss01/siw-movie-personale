package it.uniroma3.siw.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
	private String title;
    
    @NotNull
    @Min(1900)
    @Max(2023)
	private Integer year;

	private Float rating;
    
	private String urlImage;
	
	
	@ManyToOne
	private Artist director;
	
	@ManyToMany
	private Set<Artist> actors;
	
	
	@OneToOne
	@JoinColumn(name="primary_image_id")
	private Image primaryImage;

	@OneToMany(mappedBy = "movie")
	private Set<Review> reviews;
	
	@OneToMany(mappedBy = "movie")
	private Set<Image> images;

	
	
	public Movie() {
		this.rating = 0.0f;
		this.actors = new HashSet<Artist>();
		this.reviews = new HashSet<Review>();
		this.images = new HashSet<Image>();
	}

	public Float getRating() {
		return rating;
	}

	public Set<Review> getReviews() {
		return reviews;
	}

	public Set<Image> getImages() {
		return images;
	}

	

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	public void addImage(Image image){
		this.images.add(image);
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}

	public Artist getDirector() {
		return director;
	}

	public void setDirector(Artist director) {
		this.director = director;
	}

	public Set<Artist> getActors() {
		return actors;
	}

	public void setActors(Set<Artist> actors) {
		this.actors = actors;
	}

	public Image getPrimaryImage() {
		return primaryImage;
	}

	public void setPrimaryImage(Image primaryImage) {
		this.primaryImage = primaryImage;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		return Objects.equals(title, other.title) && year.equals(other.year);
	}

	
}
