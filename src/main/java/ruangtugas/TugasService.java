
package ruangtugas;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.springframework.stereotype.Repository;

@Repository
public class TugasService {

    private final SessionFactory sessionFactory;

    // Constructor Injection
    public TugasService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Menambahkan tugas
    public void simpanTugas(Tugas tugas) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.persist(tugas);

            transaction.commit();

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw ex;
        }
    }

    // Mengambil seluruh daftar tugas
    public List<Tugas> ambilSemuaTugas() {
        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                "FROM Tugas ORDER BY id DESC",
                Tugas.class
            ).getResultList();

        }
    }

    // Menghapus tugas berdasarkan ID
    public void hapusTugas(int id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Tugas tugas = session.get(Tugas.class, id);

            if (tugas != null) {
                session.remove(tugas);
            }

            transaction.commit();

        } catch (RuntimeException ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw ex;
        }
    }
}
