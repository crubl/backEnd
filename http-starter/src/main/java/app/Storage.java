package app;

import app.player.Player;
import app.tourment.Registration;
import app.tourment.Tourment;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Storage {
    public final ConcurrentHashMap<Integer, Player> players = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, Tourment> tourments = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, Registration> registrations = new ConcurrentHashMap<>();

    public final AtomicInteger playerSeq = new AtomicInteger(1);
    public final AtomicInteger tourmentSeq = new AtomicInteger(1);
    public final AtomicInteger registrationSeq = new AtomicInteger(1);
}