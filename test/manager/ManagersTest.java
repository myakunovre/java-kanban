package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void utilClassShouldReturnTaskManager() {
        assertNotNull(Managers.getDefault(), "Утилитарный класс не проинициализировал объект taskManager");
    }
    @Test
    void utilClassShouldReturnHistoryManager() {
        assertNotNull(Managers.getDefaultHistory(), "Утилитарный класс не проинициализировал объект historyManager");
    }

}