package com.vaynberg.wicket.select2;

import org.apache.wicket.model.IModel;
import org.json.JSONException;
import org.json.JSONWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Peter Nowak on 09.05.2017.
 * Single choice grouped select2 implementation for wicket based on the
 * {@link Select2GroupedMultiChoice} class from Justin Graham
 */
public class Select2GroupedChoice<T extends GroupedValue> extends Select2Choice<T> {

  /**
   * The key to group values which have null or empty groups
   */
  private static final String UNGROUPED = "UNGROUPED";

  public Select2GroupedChoice(String id, IModel<T> model,
                              ChoiceProvider<T> provider) {
    super(id, model, provider);
  }

  public Select2GroupedChoice(String id, IModel<T> model) {
    super(id, model);
  }

  public Select2GroupedChoice(String id) {
    super(id);
  }

  /**
   * Handles building the grouped JSON structure to provide to Select2
   *
   * <pre>{@code
   * {
   *   "results": [
   *     {
   *       "text": "group1",
   *       "children": [
   *         {
   *           "id": 1,
   *           "text": "Some Text"
   *         },
   *         {
   *           "id": 2,
   *           "text": "Some More Text"
   *         }
   *       ]
   *     },
   *     {
   *       "text": "group2",
   *       "children": [
   *         {
   *           "id": 3,
   *           "text": "Another Item"
   *         }
   *       ]
   *     }
   *   ],
   *   "more": true
   * }
   * }</pre>
   *
   * @param json     the writer to append the JSON nodes to
   * @param response Iterable collection of objects extending {@link GroupedValue}
   * @throws JSONException if the response contains malformed JSON
   */
  @Override
  protected void addValues(final JSONWriter json, final Iterable<T> response) throws JSONException {
    final Map<String, List<T>> groupedItems = groupItems(response);
    for (Map.Entry<String, List<T>> entry : groupedItems.entrySet()) {
      if (UNGROUPED.equals(entry.getKey())) {
        super.addValues(json, entry.getValue());
        continue;
      }
      json.object().key("text").value(entry.getKey()).key("children").array();
      super.addValues(json, entry.getValue());
      json.endArray().endObject();
    }
  }

  /**
   * Groups the {@link Iterable<T>} on the {@link GroupedValue#group}. If the value's group is null
   * or empty its added as {@link Select2GroupedChoice#UNGROUPED}
   *
   * @param response Iterable collection of objects extending {@link GroupedValue}
   * @return the sorted values
   */
  protected Map<String, List<T>> groupItems(final Iterable<T> response) {
    final Map<String, List<T>> groupedItems = new HashMap<String, List<T>>();
    for (final T item : response) {
      final String group = item.getGroup();
      final String key = (group == null || group.isEmpty()) ? UNGROUPED : group;
      if (groupedItems.containsKey(key)) {
        groupedItems.get(key).add(item);
        continue;
      }
      groupedItems.put(key, new ArrayList<T>() {{
        add(item);
      }});
    }
    return groupedItems;
  }
}
