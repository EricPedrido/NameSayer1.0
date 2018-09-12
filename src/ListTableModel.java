import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * The table model for the <code>NameSayer#_list</code> JTable.
 * This model allows for the user to add and remove creations
 * displayed on the JTable by using the corresponding buttons.
 *
 * Overrides some methods from {@link AbstractTableModel}.
 *
 * @author Eric Pedrido
 *
 * @see NameSayer
 */
public class ListTableModel extends AbstractTableModel {
    private List<String> _listCreations;

    ListTableModel() {
        _listCreations = new ArrayList<>();
    }

    public int getRowCount() {
        return _listCreations.size();
    }

    public int getColumnCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        // Return the name of the creation at this row. Column does not matter because there is only one.
        return _listCreations.get(rowIndex);
    }

    /**
     * Adds a creation to the JTable
     *
     * @param name  The name of the creation to add
     */
    public void addName(String name) {
        _listCreations.add(name);
        fireTableRowsInserted(_listCreations.size() - 1, _listCreations.size() - 1);
    }

    /**
     * Removes a creation from the JTable
     *
     * @param name  The name of the creation to remove
     */
    public void removeName(String name) {
        if (_listCreations.contains(name)) {
            _listCreations.remove(name);
            fireTableRowsDeleted(_listCreations.size() - 1, _listCreations.size() - 1);
        }
    }

    /**
     * Checks if the JTable contains the <param>name</param> entered by the user.
     *
     * @param name  The name of the creation to check
     * @return  <code>true</code> if the JTable does indeed contain that creation, otherwise <code>false</code>.
     */
    public boolean contains(String name) {
        return _listCreations.contains(name);
    }
}