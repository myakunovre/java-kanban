package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

//    Убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры
//    менеджеров;
    @Test
    void utilClassShouldReturnTaskManager() {
        assertNotNull(Managers.getDefault(), "Утилитарный класс не проинициализировал объект taskManager");
    }
    @Test
    void utilClassShouldReturnHistoryManager() {
        assertNotNull(Managers.getDefaultHistory(), "Утилитарный класс не проинициализировал объект historyManager");
    }

}