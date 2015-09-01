package idlycyme.practice.gridimagesearch.libraries;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;

import idlycyme.practice.gridimagesearch.R;

/**
 * Created by cyme on 8/30/15.
 */
public class SearchFilterDialog extends DialogFragment {
    private EditText mEditTextSite;
    private Spinner mSpinnerColor;
    private Spinner mSpinnerSize;
    private Spinner mSpinnerType;

    public interface SearchFilterDialogListener {
        void onFinishEditDialog(String site, int size, int type, int color);
    }

    public SearchFilterDialog() {
        // Empty constructor required for DialogFragment
    }

    public static SearchFilterDialog newInstance(String title, String site, int size, int type, int color) {
        SearchFilterDialog frag = new SearchFilterDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("lastSite", site);
        args.putInt("lastSize", size);
        args.putInt("lastType", type);
        args.putInt("lastColor", color);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filters, container);
        mEditTextSite = (EditText)view.findViewById(R.id.etSite);
        mSpinnerColor = (Spinner)view.findViewById(R.id.spFilterColor);
        mSpinnerSize = (Spinner)view.findViewById(R.id.spFilterSize);
        mSpinnerType = (Spinner)view.findViewById(R.id.spFilterType);

        int lastType = getArguments().getInt("lastType");
        int lastColor = getArguments().getInt("lastColor");
        int lastSize = getArguments().getInt("lastSize");
        String lastSite = getArguments().getString("lastSite");
        String title = getArguments().getString("title", "Search Filters");

        getDialog().setTitle(title);
        mSpinnerColor.setSelection(lastColor);
        mSpinnerType.setSelection(lastType);
        mSpinnerSize.setSelection(lastSize);
        mEditTextSite.setText(lastSite);

        mEditTextSite.requestFocus();

        // Show soft keyboard automatically
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return view;
    }

    private void triggerListenerOnSettingChange() {
        SearchFilterDialogListener listener = (SearchFilterDialogListener) getActivity();
        int color =  mSpinnerColor.getSelectedItemPosition();
        int type = mSpinnerType.getSelectedItemPosition();
        int size = mSpinnerSize.getSelectedItemPosition();
        String site = mEditTextSite.getText().toString();
        listener.onFinishEditDialog(site, size, type, color);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        triggerListenerOnSettingChange();
        super.onDismiss(dialog);
    }
}
