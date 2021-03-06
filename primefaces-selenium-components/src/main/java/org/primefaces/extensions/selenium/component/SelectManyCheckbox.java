/*
 * Copyright 2011-2020 PrimeFaces Extensions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.extensions.selenium.component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.extensions.selenium.PrimeExpectedConditions;
import org.primefaces.extensions.selenium.PrimeSelenium;
import org.primefaces.extensions.selenium.component.base.AbstractComponent;
import org.primefaces.extensions.selenium.component.base.ComponentUtils;
import org.primefaces.extensions.selenium.component.model.SelectItem;

public abstract class SelectManyCheckbox extends AbstractComponent {

    @FindBy(css = ".ui-chkbox")
    private List<WebElement> checkboxes;

    public List<WebElement> getCheckboxes() {
        return checkboxes;
    }

    public void toggle(int... indexes) {
        for (int i : indexes) {
            WebElement checkbox = getCheckboxes().get(i);
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(checkbox));

            WebElement input = checkbox.findElement(By.tagName("input"));
            if (ComponentUtils.isAjaxScript(input.getAttribute("onchange"))) {
                PrimeSelenium.guardAjax(checkbox).click();
            }
            else {
                checkbox.click();
            }
        }
    }

    public void toggleAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            toggle(i);
        }
    }

    public void select(int... indexes) {
        deselectAll();

        for (int i : indexes) {
            if (!isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void selectAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            if (!isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void deselect(int... indexes) {
        for (int i : indexes) {
            if (isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void deselectAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            if (isSelected(i)) {
                toggle(i);
            }
        }
    }

    public int getItemsSize() {
        return getCheckboxes().size();
    }

    public List<String> getLabels() {
        return getCheckboxes().stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
    }

    public String getLabel(int index) {
        return getItems().get(index).getLabel();
    }

    public List<SelectItem> getItems() {
        ArrayList<SelectItem> items = new ArrayList<>();

        int idx = 0;
        for (WebElement checkbox : getCheckboxes()) {
            WebElement input = checkbox.findElement(By.tagName("input"));
            WebElement label = getRoot().findElement(By.cssSelector("label[for='" + input.getAttribute("id") + "']"));
            WebElement box = checkbox.findElement(By.className("ui-chkbox-box"));

            SelectItem item = new SelectItem();
            item.setIndex(idx);
            item.setLabel(label.getText());
            item.setValue(input.getAttribute("value"));
            item.setSelected(PrimeSelenium.hasCssClass(box, "ui-state-active"));
            items.add(item);

            idx++;
        }

        return items;
    }

    public boolean isSelected(int index) {
        return getItems().get(index).isSelected();
    }
}
