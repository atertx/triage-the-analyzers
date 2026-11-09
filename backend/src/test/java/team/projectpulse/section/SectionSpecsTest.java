package team.projectpulse.section;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Shows PMD's UseLocaleWithCaseConversions finding in SectionSpecs.containsSectionName.
 * The search text is lowercased with the server's default locale. On a Turkish server,
 * capital I becomes a dotless i, so the search pattern no longer matches the section
 * name the database lowercased, and the search finds nothing.
 */
class SectionSpecsTest {

    private Locale originalLocale;

    @BeforeEach
    void rememberLocale() {
        originalLocale = Locale.getDefault();
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(originalLocale);
    }

    @Test
    @SuppressWarnings("unchecked")
    void sectionNameSearchDoesNotDependOnTheServerLocale() {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        Root<Section> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);

        SectionSpecs.containsSectionName("SECTION").toPredicate(root, query, criteriaBuilder);

        ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
        verify(criteriaBuilder).like(any(), pattern.capture());
        assertThat(pattern.getValue()).isEqualTo("%section%");
    }
}