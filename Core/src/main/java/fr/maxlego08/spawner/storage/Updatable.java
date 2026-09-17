package fr.maxlego08.spawner.storage;

import fr.maxlego08.spawner.zcore.utils.ZUtils;

public abstract class Updatable extends ZUtils {

    /**
     * Signale une mutation à persister.
     * <p>
     * Cette méthode ne déclenchait auparavant {@link #save()} qu'une fois sur cinq, pour
     * amortir le coût de l'upsert qui était en O(n) sur le tampon d'écriture. Les quatre
     * autres mutations n'étaient jamais mises en attente et disparaissaient en cas de crash.
     * L'upsert étant désormais en O(1) dans {@link TypeSafeCache}, et l'écriture en base
     * restant groupée par la tâche batch, il n'y a plus de raison de filtrer.
     */
    public void canUpdate() {
        this.save();
    }

    public abstract void save();

}
