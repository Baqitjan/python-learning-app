package com.example.mobileapp.ui.compiler;
import androidx.lifecycle.Observer;

/**
 * LiveData мәнін тек бір рет тұтынуға мүмкіндік беретін класс.
 * Бұл бір реттік оқиғалар (мысалы, навигация, Toast хабарламасы) үшін қолданылады,
 * осылайша конфигурация өзгергенде (мысалы, экранды бұрғанда) қайта іске қосылмайды.
 */
public class Event<T> {

    private T content;

    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Мазмұнды қайтарады және оның өңделгенін белгілейді.
     * Егер мазмұн бұрын өңделген болса, null қайтарады.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Мазмұнның өңделген-өңделмегенін қарамастан, оны қайтарады.
     */
    public T peekContent() {
        return content;
    }
}
