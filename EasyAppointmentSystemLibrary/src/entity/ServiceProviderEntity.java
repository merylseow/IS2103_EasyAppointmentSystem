package entity;

import java.io.Serializable;
import java.security.CryptoPrimitive;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;
import util.enumeration.StatusEnum;
import util.security.CryptographicHelper;

@Entity

public class ServiceProviderEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceProviderId;
    @Column(nullable = false, length = 32, unique = true)
    private String name;
    @Column(nullable = false)
    private Integer businessCategory;
    @Column(nullable = false, unique = true)
    private Long registrationNumber;
    @Column(nullable = false, length = 32)
    private String city;
    @Column(nullable = false, length = 32)
    private String address;
    @Column(nullable = false, unique = true, length = 32)
    private String email;
    @Column(nullable = false, length = 32)
    private String phone;
    private String password;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    private Double rating = 0.0;
    private List<Double> ratingsList = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CategoryEntity category;

    @OneToMany(mappedBy = "serviceProviderEntity")
    private List<AppointmentEntity> appointments;

    @Column(columnDefinition = "CHAR(32) NOT NULL")
    private String salt;

    public ServiceProviderEntity() {
        this.appointments = new ArrayList<>();
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
    }

    public ServiceProviderEntity(String name, Integer businessCategory, Long registrationNumber, String city, String address, String email, String phone, String password, StatusEnum status, Double rating, List<Double> ratingsList, CategoryEntity category) {
        this();
        this.name = name;
        this.businessCategory = businessCategory;
        this.registrationNumber = registrationNumber;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.status = status;
        this.rating = rating;
        this.ratingsList = ratingsList;
        this.category = category;
        setPassword(password);
    }

    public ServiceProviderEntity(String name, Integer businessCategory, Long registrationNumber, String city, String address, String email, String phone, String password, StatusEnum status, double rating) {
        this();
        this.name = name;
        this.businessCategory = businessCategory;
        this.registrationNumber = registrationNumber;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.status = status;
        this.rating = rating;
        setPassword(password);
    }

    public Long getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceProviderId != null ? serviceProviderId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the serviceProviderId fields are not set
        if (!(object instanceof ServiceProviderEntity)) {
            return false;
        }
        ServiceProviderEntity other = (ServiceProviderEntity) object;
        if ((this.serviceProviderId == null && other.serviceProviderId != null) || (this.serviceProviderId != null && !this.serviceProviderId.equals(other.serviceProviderId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ServiceProviderEntity[ id=" + serviceProviderId + " ]";
    }

    public Integer getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(Integer businessCategory) {
        this.businessCategory = businessCategory;
    }

    public Long getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(Long registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + this.salt));
        } else {
            this.password = null;
        }
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @XmlTransient
    public List<AppointmentEntity> getAppointments() {
        return appointments;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setAppointments(List<AppointmentEntity> appointments) {
        this.appointments = appointments;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getRatingsList() {
        return ratingsList;
    }

    public void setRatingsList(List<Double> ratingsList) {
        this.ratingsList = ratingsList;
    }

    // record every time the service provider makes a new appointment
    public void addToServiceProvAppointments(AppointmentEntity appt) {
        if (!this.appointments.contains(appt)) {
            this.appointments.add(appt);
        } else {
            System.out.println("Appointment already exist");
        }
    }

    // record every time a new rating is added
    public void addToRatingsList(Double rating) {
        this.ratingsList.add(rating);
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
