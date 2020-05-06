package org.smartregister.chw.core.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.holders.FooterViewHolder;
import org.smartregister.chw.core.holders.ReferralNotificationViewHolder;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.family.fragment.BaseFamilyRegisterFragment;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;

public class BaseReferralNotificationProvider implements RecyclerViewProvider<ReferralNotificationViewHolder> {

    private Context context;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;

    public BaseReferralNotificationProvider(Context context, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, ReferralNotificationViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        populatePatientColumn(pc, viewHolder);
    }

    private void populatePatientColumn(CommonPersonObjectClient client, ReferralNotificationViewHolder viewHolder) {

        String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String fullName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);
        String dobString = Utils.getDuration(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, false));
        String translatedYearInitial = context.getResources().getString(org.smartregister.opd.R.string.abbrv_years);
        viewHolder.setNameAndAge(StringUtils.capitalize(fullName) + ", " + StringUtils.capitalize(OpdUtils.getClientAge(dobString, translatedYearInitial)));

        String notificationType = Utils.getValue(client.getColumnmaps(), CoreConstants.DB_CONSTANTS.NOTIFICATION_TYPE, true);
        viewHolder.setReferralTypeTextView(notificationType);

        String notificationEventDate = Utils.getValue(client.getColumnmaps(), CoreConstants.DB_CONSTANTS.NOTIFICATION_DATE, false);
        if (StringUtils.isNotBlank(notificationEventDate)) {
            DateTime duration = new DateTime(DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(notificationEventDate));
            viewHolder.setNotificationDate(org.smartregister.chw.core.utils.Utils.formatReferralDuration(duration, context));
        }
        attachPatientOnclickListener(viewHolder.itemView, client);
    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseFamilyRegisterFragment.CLICK_VIEW_NORMAL);
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNextPage, boolean hasPreviousPage) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(R.string.str_page_info), currentPageCount, totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNextPage ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPreviousPage ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//doNothing
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ReferralNotificationViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater().inflate(R.layout.referral_notification_list_row, parent, false);
        return new ReferralNotificationViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater().inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }
}
