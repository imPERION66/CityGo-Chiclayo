package com.greencix.citygo.ui.colaborador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.greencix.citygo.data.model.EducaInsignia;
import com.greencix.citygo.data.network.Callback;
import com.greencix.citygo.data.repository.EducaRepository;
import com.greencix.citygo.databinding.FragmentColaboradorNivelBinding;
import com.greencix.citygo.ui.adapters.InsigniasAdapter;
import java.util.List;

public class ColaboradorNivelFragment extends Fragment {

    private FragmentColaboradorNivelBinding binding;

    public static ColaboradorNivelFragment newInstance() {
        return new ColaboradorNivelFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentColaboradorNivelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvInsigniasColaborador.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarInsigniasLaborales();
    }

    private void cargarInsigniasLaborales() {
        EducaRepository.getInstance().getInsignias("colaborador", new Callback<List<EducaInsignia>>() {
            @Override
            public void onSuccess(List<EducaInsignia> insignias) {
                if (binding == null || getContext() == null) return;
                InsigniasAdapter adapter = new InsigniasAdapter(getContext(), insignias);
                binding.rvInsigniasColaborador.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
