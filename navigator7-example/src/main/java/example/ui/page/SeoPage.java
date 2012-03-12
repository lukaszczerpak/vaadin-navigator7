package example.ui.page;

import org.vaadin.navigator7.Page;
import org.vaadin.navigator7.PageLink;
import org.vaadin.navigator7.ParamPageLink;

import com.vaadin.ui.VerticalLayout;

import example.model.Product;

/** Demo of the EntityUriAnalayzer (MyUriAnalyzer) with @Param (and the ParamInjectInterceptor). */
@Page(crawlable=true)
public class SeoPage extends VerticalLayout {

    public SeoPage() {
        // Fake product.
        Product p = new Product();
        p.setId(34L);

        // Links.
        addComponent( new ParamPageLink("Link to @Page(crawlable=true) ProduceAPage", ProductAPage.class, p));
        addComponent( new PageLink("Link to @Page(crawlable=false) ProduceBPage", ProductBPage.class, p.getId().toString()));
        addComponent( new PageLink("Link to @Page(crawlable=true) SeoPage (this page)", SeoPage.class));
    }
    
}
