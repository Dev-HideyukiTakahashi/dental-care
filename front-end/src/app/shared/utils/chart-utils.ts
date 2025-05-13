import { ChartConfiguration } from 'chart.js';
import { IDentistMin } from '../../model/dentist-min.model';
import { DashboardChartData } from './chart-types';

export const getDashboardChartConfig = (): ChartConfiguration<'bar'>['options'] => ({
  responsive: true,
  maintainAspectRatio: false,
  indexAxis: 'y',
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      backgroundColor: '#495057',
      titleColor: '#ffffff',
      bodyColor: '#f8f9fa',
      borderColor: '#6c757d',
      borderWidth: 1,
      callbacks: {
        label: (context) => `Nota: ${context.raw}`,
      },
    },
  },
  scales: {
    x: {
      min: 0,
      max: 10,
      ticks: {
        stepSize: 1,
        color: '#f8f9fa',
      },
      grid: {
        color: '#495057',
      },
    },
    y: {
      ticks: {
        color: '#f8f9fa',
      },
      grid: {
        color: '#495057',
      },
    },
  },
});

export const initialChartData: DashboardChartData = {
  labels: [],
  datasets: [
    {
      label: 'Nota',
      data: [],
      backgroundColor: '#0d6efd',
      borderColor: '#0b5ed7',
      borderWidth: 1,
      borderRadius: 4,
      hoverBackgroundColor: '#0b5ed7',
    },
  ],
};

export const transformToChartData = (dentists: IDentistMin[]) => {
  return {
    labels: dentists.map((d) => d.name),
    datasets: [
      {
        label: 'Nota',
        data: dentists.map((d) => d.score),
        backgroundColor: '#0d6efd',
        borderColor: '#0b5ed7',
        borderWidth: 1,
        borderRadius: 4,
        hoverBackgroundColor: '#0b5ed7',
      },
    ],
  };
};
