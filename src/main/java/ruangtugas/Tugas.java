package ruangtugas;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tugas")
public class Tugas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nama_tugas", nullable = false, length = 100)
    private String namaTugas;

    @Column(name = "mata_kuliah", nullable = false, length = 100)
    private String mataKuliah;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    // Constructor kosong
    public Tugas() {
    }

    // Constructor dengan parameter
    public Tugas(String namaTugas, String mataKuliah,
                 LocalDate deadline, String status) {
        this.namaTugas = namaTugas;
        this.mataKuliah = mataKuliah;
        this.deadline = deadline;
        this.status = status;
    }

    // Getter dan Setter ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter dan Setter Nama Tugas
    public String getNamaTugas() {
        return namaTugas;
    }

    public void setNamaTugas(String namaTugas) {
        this.namaTugas = namaTugas;
    }

    // Getter dan Setter Mata Kuliah
    public String getMataKuliah() {
        return mataKuliah;
    }

    public void setMataKuliah(String mataKuliah) {
        this.mataKuliah = mataKuliah;
    }

    // Getter dan Setter Deadline
    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    // Getter dan Setter Status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}