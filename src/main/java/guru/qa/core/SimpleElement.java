package guru.qa.core;

import guru.qa.core.config.Config;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static guru.qa.core.Utils.sleep;

public class SimpleElement implements WebElement {

    private final By locator;
    private WebElement delegate;

    public SimpleElement(By locator) {
        this.locator = locator;
    }

    private SimpleElement(WebElement webElement) {
        locator = null;
        this.delegate = webElement;
    }

    public static SimpleElement wrap(WebElement source) {
        return new SimpleElement(source);
    }

    @Override
    public void click() {
        execute(WebElement::click);
    }

    @Override
    public void submit() {
        execute(WebElement::submit);
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        execute(webElement -> {
            if (keysToSend != null && keysToSend[0] == null) {
                webElement.sendKeys("");
            } else {
                webElement.sendKeys(keysToSend);
            }
        });
    }

    @Override
    public void clear() {
        execute(WebElement::clear);
    }

    @Override
    public String getTagName() {
        return execute(WebElement::getTagName);
    }

    @Override
    public String getDomProperty(String name) {
        return execute(webElement -> {
            return webElement.getDomProperty(name);
        });
    }

    @Override
    public String getDomAttribute(String name) {
        return execute(webElement -> {
            return webElement.getDomAttribute(name);
        });
    }

    @Override
    public String getAttribute(String name) {
        return execute(webElement -> {
            return webElement.getAttribute(name);
        });
    }

    @Override
    public String getAriaRole() {
        return execute(WebElement::getAriaRole);
    }

    @Override
    public String getAccessibleName() {
        return execute(WebElement::getAccessibleName);
    }

    @Override
    public boolean isSelected() {
        return execute(WebElement::isSelected);
    }

    @Override
    public boolean isEnabled() {
        return execute(WebElement::isEnabled);
    }

    @Override
    public String getText() {
        return execute(WebElement::getText);
    }

    @Override
    public List<WebElement> findElements(By by) {
        return SimpleElementLocator.INSTANCE.findElements(WebDriverContainer.INSTANCE.getRequiredWebDriver(), by);
    }

    @Override
    public WebElement findElement(By by) {
        return SimpleElementLocator.INSTANCE.findElement(WebDriverContainer.INSTANCE.getRequiredWebDriver(), by);
    }

    @Override
    public SearchContext getShadowRoot() {
        return execute(WebElement::getShadowRoot);
    }

    @Override
    public boolean isDisplayed() {
        return execute(WebElement::isDisplayed);
    }

    @Override
    public Point getLocation() {
        return execute(WebElement::getLocation);
    }

    @Override
    public Dimension getSize() {
        return execute(WebElement::getSize);
    }

    @Override
    public Rectangle getRect() {
        return execute(WebElement::getRect);
    }

    @Override
    public String getCssValue(String propertyName) {
        return execute(webElement -> {
            return webElement.getCssValue(propertyName);
        });
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return execute(webElement -> {
            return webElement.getScreenshotAs(target);
        });
    }

    private void execute(@Nonnull Consumer<WebElement> action) {
        checkDelegate();
        StopWatch stopWatch = StopWatch.createStarted();
        while (stopWatch.getTime() <= Config.INSTANCE.actionTimeout) {
            try {
                action.accept(delegate);
                return;
            } catch (Exception e) {
                sleep(250);
            }
        }
        action.accept(delegate);
    }

    @Nullable
    private <T> T execute(@Nonnull Function<WebElement, T> action) {
        checkDelegate();
        StopWatch stopWatch = StopWatch.createStarted();
        while (stopWatch.getTime() <= Config.INSTANCE.actionTimeout) {
            try {
                return action.apply(delegate);
            } catch (Exception e) {
                sleep(250);
            }
        }
        return action.apply(delegate);
    }

    private void checkDelegate() {
        if (delegate == null) {
            delegate = SimpleElementLocator.INSTANCE.findElement(WebDriverContainer.INSTANCE.getRequiredWebDriver(), locator);
        }
    }
}
