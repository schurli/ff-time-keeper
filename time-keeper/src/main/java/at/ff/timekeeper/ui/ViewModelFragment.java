package at.ff.timekeeper.ui;

import androidx.fragment.app.Fragment;

import at.ff.timekeeper.vm.BasicViewModel;


public abstract class ViewModelFragment extends Fragment implements IViewModelFragment {

    private BasicViewModel viewModel;

    public void setViewModel(BasicViewModel viewModel) {
        this.viewModel= viewModel;
    }

    public BasicViewModel getViewModel() {
        return viewModel;
    }

    public abstract void bind();

    public abstract void unbind();

}
