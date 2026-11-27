package app.tourment;

import app.Storage;
import app.exception.ConflictException;
import app.exception.NotFoundException;
import app.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TourmentServiceTest {

    private Storage store;
    private TourmentService service;

    @BeforeEach
    void setUp() {
        store = new Storage();
        service = new TourmentService(store);
    }

    @Test
    void create_withValidData_returnsOpenTourment() {
        Tourment t = service.create("NOOB", "Cup", 1, 0, 2, 7);
        assertThat(t).isInstanceOf(NoobTourment.class);
        assertThat(t.getStatus()).isEqualTo(Tourment.Status.Open);
        assertThat(store.tourments).containsKey(t.getId());
    }

    @Test
    void create_withBlankName_throwsValidationException() {
        assertThatThrownBy(() -> service.create("NOOB", "", 1, 0, 2, 7))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void create_withUnknownType_throwsValidationException() {
        assertThatThrownBy(() -> service.create("GOD", "Cup", 1, 0, 2, 7))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getById_whenNotExists_throwsNotFoundException() {
        assertThatThrownBy(() -> service.getById(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void changeStatus_openToGoing_whenEnoughRegistered() {
        Tourment t = service.create("NOOB", "Cup", 1, 0, 1, 7);
        store.registrations.put(1, new Registration(1, t.getId(), 1));
        service.changeStatus(t.getId());
        assertThat(t.getStatus()).isEqualTo(Tourment.Status.Going);
    }

    @Test
    void changeStatus_openToGoing_whenNotEnoughRegistered_throwsConflict() {
        Tourment t = service.create("NOOB", "Cup", 1, 0, 3, 7);
        assertThatThrownBy(() -> service.changeStatus(t.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Недостаточно участников");
    }

    @Test
    void changeStatus_goingToFinished() {
        Tourment t = service.create("NOOB", "Cup", 1, 0, 1, 7);
        store.registrations.put(1, new Registration(1, t.getId(), 1));
        service.changeStatus(t.getId());
        service.changeStatus(t.getId());
        assertThat(t.getStatus()).isEqualTo(Tourment.Status.Finished);
    }

    @Test
    void changeStatus_finished_throwsConflict() {
        Tourment t = service.create("NOOB", "Cup", 1, 0, 1, 7);
        store.registrations.put(1, new Registration(1, t.getId(), 1));
        service.changeStatus(t.getId());
        service.changeStatus(t.getId());
        assertThatThrownBy(() -> service.changeStatus(t.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("уже закончился");
    }

    @Test
    void delete_removesTourmentAndRelatedRegistrations() {
        Tourment t = service.create("NOOB", "Cup", 1, 0, 1, 7);
        store.registrations.put(1, new Registration(1, t.getId(), 1));
        service.delete(t.getId());
        assertThat(store.tourments).doesNotContainKey(t.getId());
        assertThat(store.registrations).isEmpty();
    }

    @Test
    void getAll_filtersByType() {
        service.create("NOOB", "Cup1", 1, 0, 1, 7);
        service.create("PRO", "Cup2", 1, 0, 1, 7);
        assertThat(service.getAll("PRO")).hasSize(1);
    }
}