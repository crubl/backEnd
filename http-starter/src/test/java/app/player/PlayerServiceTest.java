package app.player;

import app.Storage;
import app.exception.NotFoundException;
import app.exception.ValidationException;
import app.tourment.Registration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PlayerServiceTest {

    private Storage store;
    private PlayerService service;

    @BeforeEach
    void setUp() {
        store = new Storage();
        service = new PlayerService(store);
    }

    @Test
    void create_withValidData_returnsPlayerAndStoresIt() {
        Player p = service.create("Ivan", "NOOB");
        assertThat(p).isInstanceOf(NoobPlayer.class);
        assertThat(p.getName()).isEqualTo("Ivan");
        assertThat(store.players).containsKey(p.getId());
    }

    @Test
    void create_withBlankName_throwsValidationException() {
        assertThatThrownBy(() -> service.create("", "NOOB"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void create_withNullType_throwsValidationException() {
        assertThatThrownBy(() -> service.create("Ivan", null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void create_withUnknownType_throwsValidationException() {
        assertThatThrownBy(() -> service.create("Ivan", "GOD"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Неизвестный тип");
    }

    @Test
    void getById_whenNotExists_throwsNotFoundException() {
        assertThatThrownBy(() -> service.getById(999))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getById_whenIdNotPositive_throwsValidationException() {
        assertThatThrownBy(() -> service.getById(0))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void addExp_withNegativeAmount_throwsValidationException() {
        Player p = service.create("Ivan", "NOOB");
        assertThatThrownBy(() -> service.addExp(p.getId(), -5))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void addExp_updatesPlayerExp() {
        Player p = service.create("Ivan", "NOOB");
        service.addExp(p.getId(), 100);
        assertThat(service.getById(p.getId()).getExp()).isEqualTo(100);
    }

    @Test
    void update_changesName() {
        Player p = service.create("Ivan", "NOOB");
        service.update(p.getId(), "Petr");
        assertThat(service.getById(p.getId()).getName()).isEqualTo("Petr");
    }

    @Test
    void delete_removesPlayerAndRelatedRegistrations() {
        Player p = service.create("Ivan", "NOOB");
        store.registrations.put(1, new Registration(1, 1, p.getId()));
        service.delete(p.getId());
        assertThat(store.players).doesNotContainKey(p.getId());
        assertThat(store.registrations).isEmpty();
    }

    @Test
    void getAll_filtersByType() {
        service.create("Ivan", "NOOB");
        service.create("Petr", "PRO");
        List<Player> noobs = service.getAll("NOOB");
        assertThat(noobs).hasSize(1);
        assertThat(noobs.get(0).type()).isEqualTo("Noob");
    }

    @Test
    void getAll_withoutFilter_returnsAll() {
        service.create("Ivan", "NOOB");
        service.create("Petr", "PRO");
        assertThat(service.getAll(null)).hasSize(2);
    }
}