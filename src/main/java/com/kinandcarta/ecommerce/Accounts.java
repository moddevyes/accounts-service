package com.kinandcarta.ecommerce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@Builder
@AllArgsConstructor
@Entity
@Table(name = "accounts")
@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @NotNull
    @Size(min = 6, max = 255, message = "Account reference ID must be between 6 and 255 characters")
    private String accountRefId;

    @NotNull
    @Size(min = 2, max = 200, message = "First Name must be between 2 and 200 characters")
    @Column(columnDefinition = "varchar(200) default ''", nullable = false)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 200, message = "Last Name must be between 2 and 200 characters")
    @Column(columnDefinition = "varchar(200) default ''", nullable = false)
    private String lastName;

    @NotNull
    @Email(message = "Invalid e-mail address.")
    @Size(min = 3, max = 200, message = "Email address must be between 3 and 200 characters")
    @Column(columnDefinition = "varchar(200) default ''", nullable = false, unique = true)
    private String emailAddress;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Address> addresses;

    @CreationTimestamp
    @Column(name="created_dt")
    private Instant createDateTime;

    @UpdateTimestamp
    @Column(name="updated_dt")
    private Instant updateDateTime;

    // NOTES: HibernateProxy, https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Accounts accounts = (Accounts) o;
        return getId() != null && Objects.equals(getId(), accounts.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
