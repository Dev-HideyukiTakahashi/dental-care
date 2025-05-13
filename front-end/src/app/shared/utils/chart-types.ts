import { ChartConfiguration } from 'chart.js';

export type DashboardChartType = 'bar';

export interface DashboardChartData {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor: string;
    borderColor: string;
    borderWidth: number;
    borderRadius: number;
    hoverBackgroundColor: string;
  }[];
}

export type DashboardChartOptions = ChartConfiguration<'bar'>['options'];
