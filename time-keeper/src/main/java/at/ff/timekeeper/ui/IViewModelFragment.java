package at.ff.timekeeper.ui;


import at.ff.timekeeper.vm.BasicViewModel;

public interface IViewModelFragment {

    void setViewModel(BasicViewModel viewModel);

    BasicViewModel getViewModel();

    void bind();

    void unbind();

}
