package org.rococo.jupiter.extension;

import jakarta.persistence.EntityManagerFactory;
import org.rococo.data.jpa.EmfContext;

public class JpaExtension implements SuiteExtension {
    @Override
    public void afterSuite() {
        EmfContext.INSTANCE.storedEmf().forEach(EntityManagerFactory::close);
    }
}
