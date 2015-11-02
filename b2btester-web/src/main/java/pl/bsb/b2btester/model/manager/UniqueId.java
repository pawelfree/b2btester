package pl.bsb.b2btester.model.manager;

import java.io.Serializable;
import java.util.Calendar;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 *
 * @author paweld
 */
@Named
@Singleton
public class UniqueId implements Serializable {

    private static final long serialVersionUID = 7L;
    /**
     * place to store count
     */
    private volatile long theCount;

    @PostConstruct
    public void initialize() {
        Calendar now = Calendar.getInstance();
        theCount = now.getTimeInMillis();
    }

    /**
     * gets the next value of the counter. If you call it frequently, it will use the incremented count, but if time
     * elapses, it uses the current time
     *
     * @return the next value of the timer
     */
    private synchronized Long next() {
        return next(0);
    }

    /**
     * gets the next value of the counter. If you call it frequently, it will use the incremented count, but if time
     * elapses, it uses the current time
     *
     * @param delay millisecond increment
     * @return the next value of the timer
     */
    private synchronized Long next(int delay) {
        Calendar now = Calendar.getInstance();
        long tempTime = now.getTimeInMillis() + delay;
        theCount++;
        if (theCount < tempTime) {
            theCount = tempTime;
        }
        return theCount;
    }

    public String nextId() {
        return Long.toString(next());
    }
}
