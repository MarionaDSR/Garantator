package es.dsrroma.garantator.data.model;

import java.util.Date;

public class Warranty extends AbstractModel {
    private Product product;
    private Date startDate;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
