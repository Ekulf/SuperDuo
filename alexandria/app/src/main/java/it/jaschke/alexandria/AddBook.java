package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private static final int LOADER_ID = 1;
    private static final int RC_SCAN = 100;
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";
    private static final String EAN_CONTENT = "eanContent";
    private static final String LAST_GOOD_EAN = "last_good_ean";

    private EditText mEanEditText;
    private TextView mBookTitleView;
    private TextView mBookSubtitleView;
    private TextView mAuthorsView;
    private TextView mCategoriesView;
    private ImageView mCoverImageView;
    private View mSaveButton;
    private View mDeleteButton;

    private String mLastGoodEan;
    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    public AddBook() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEanEditText != null) {
            outState.putString(EAN_CONTENT, mEanEditText.getText().toString());
        }

        outState.putString(LAST_GOOD_EAN, mLastGoodEan);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        mEanEditText = (EditText) rootView.findViewById(R.id.ean);

        mBookTitleView = (TextView) rootView.findViewById(R.id.bookTitle);
        mBookSubtitleView = (TextView) rootView.findViewById(R.id.bookSubTitle);
        mAuthorsView = (TextView) rootView.findViewById(R.id.authors);
        mCategoriesView = (TextView) rootView.findViewById(R.id.categories);
        mCoverImageView = (ImageView) rootView.findViewById(R.id.bookCover);
        mSaveButton = rootView.findViewById(R.id.save_button);
        mDeleteButton = rootView.findViewById(R.id.delete_button);

        if (savedInstanceState != null) {
            mEanEditText.setText(savedInstanceState.getString(EAN_CONTENT));
            mEanEditText.setHint("");

            mLastGoodEan = savedInstanceState.getString(LAST_GOOD_EAN);
            if (!TextUtils.isEmpty(mLastGoodEan)) {
                AddBook.this.restartLoader();
            }
        }

        mEanEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ean = s.toString();

                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }

                if (ean.length() != 13) {
                    return;
                }

                mLastGoodEan = ean;
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mLastGoodEan);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                startActivityForResult(intent, RC_SCAN);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEanEditText.setText("");
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mEanEditText.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                mEanEditText.setText("");
                clearFields();
                mLastGoodEan = null;
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEanEditText = null;
        mBookTitleView = null;
        mBookSubtitleView = null;
        mAuthorsView = null;
        mCategoriesView = null;
        mCoverImageView = null;
        mSaveButton = null;
        mDeleteButton = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SCAN) {
            if (resultCode == Activity.RESULT_OK &&
                    data != null &&
                    data.hasExtra(ScannerActivity.RESULT_BAR_CODE)) {
                String code = data.getStringExtra(ScannerActivity.RESULT_BAR_CODE);
                if (!TextUtils.isEmpty(code)) {
                    mEanEditText.setText(code);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (TextUtils.isEmpty(mLastGoodEan)) {
            return null;
        }

        String eanStr = mLastGoodEan;
        if (eanStr.length() == 10 && !eanStr.startsWith("978")) {
            eanStr = "978" + eanStr;
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        // Make sure we have data and the views are available.
        if (!data.moveToFirst() || mBookTitleView == null) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitleView.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mBookSubtitleView.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        mAuthorsView.setLines(authorsArr.length);
        mAuthorsView.setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            new DownloadImage(mCoverImageView).execute(imgUrl);
            mCoverImageView.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mCategoriesView.setText(categories);

        mSaveButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    private void clearFields() {
        mBookTitleView.setText("");
        mBookSubtitleView.setText("");
        mAuthorsView.setText("");
        mCategoriesView.setText("");
        mCoverImageView.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }
}
