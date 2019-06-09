package se.jsquad.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("PREMIUM")
public class PremiumClient extends ClientType {
    @Column(name = "SPECIALOFFERS")
    private String specialOffers;

    @NotNull
    @Column(name = "PREMIUMRATING")
    private Long premiumRating;

    public String getSpecialOffers() {
        return specialOffers;
    }

    public void setSpecialOffers(String specialOffers) {
        this.specialOffers = specialOffers;
    }

    public Long getPremiumRating() {
        return premiumRating;
    }

    public void setPremiumRating(Long premiumRating) {
        this.premiumRating = premiumRating;
    }
}
